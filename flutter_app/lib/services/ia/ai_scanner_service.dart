import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:image_picker/image_picker.dart';
import 'package:google_mlkit_text_recognition/google_mlkit_text_recognition.dart';
import 'package:google_generative_ai/google_generative_ai.dart';
import '../../models/app_state.dart';

class AIScannerService {
  static Future<List<ItemModel>> scanReceipt(XFile imageFile) async {
    try {
      // 1. Estrazione Testo con Google ML Kit (Offline e velocissimo)
      final inputImage = InputImage.fromFilePath(imageFile.path);
      final textRecognizer =
          TextRecognizer(script: TextRecognitionScript.latin);
      final RecognizedText recognizedText =
          await textRecognizer.processImage(inputImage);
      await textRecognizer.close();

      String rawText = recognizedText.text;

      if (rawText.trim().isEmpty) {
        throw Exception("Nessun testo trovato nell'immagine.");
      }

      // 2. Lettura API Key da .env
      final apiKey = dotenv.env['GEMINI_API_KEY'];
      if (apiKey == null || apiKey.isEmpty) {
        throw Exception(
            "Chiave API GEMINI_API_KEY non trovata nel file .env.");
      }

      // 3. Chiamata a Gemini Flash (Gratuito, limite alto, ultima generazione)
      final model = GenerativeModel(
        model: 'gemini-flash-latest',
        apiKey: apiKey,
        generationConfig: GenerationConfig(
          temperature: 0.1,
          responseMimeType: 'application/json',
        ),
      );

      final prompt = '''
Sei un esperto estrattore di dati da scontrini della spesa.
Ti verrà fornito il testo grezzo estratto da uno scontrino tramite OCR.
Il tuo compito è analizzare il testo ed estrarre ESCLUSIVAMENTE gli articoli della spesa acquistati, ignorando tutto il resto (es. sconti, subtotali, totale, carte di credito, IVA, resto, intestazione negozio, indirizzo, ecc.).

REGOLE FONDAMENTALI:
1. Restituisci SOLO un array JSON valido, senza testo aggiuntivo e senza markdown (no ```json).
2. Per ogni articolo trovato, crea un oggetto JSON con questi campi:
   - "name": Il nome dell'articolo (pulito, senza abbreviazioni incomprensibili, capitalizzato bene. Rimuovi la grammatura o i prezzi dal nome).
   - "quantity": La quantità dell'articolo (numero intero, solitamente 1 se non specificato).
   - "category": La categoria del prodotto. Usa SOLO una delle seguenti categorie: "Frutta & Verdura", "Carne & Pesce", "Latticini & Uova", "Dispensa", "Bevande", "Surgelati", "Cura Personale", "Pulizia Casa", "Altro".
3. Ignora le righe che contengono parole come "sconto", "totale", "resto", "pagamento", "filiera", "srl", "iva", "bancomat", ecc.

Testo OCR dello scontrino:
$rawText
''';

      final response = await model.generateContent([Content.text(prompt)]);
      
      String content = response.text ?? '[]';

      // Pulizia di eventuali backtick di markdown
      content =
          content.replaceAll('```json', '').replaceAll('```', '').trim();

      List<dynamic> jsonList = jsonDecode(content);
      List<ItemModel> extractedItems = [];

      for (var item in jsonList) {
        extractedItems.add(ItemModel(
          id: DateTime.now().millisecondsSinceEpoch.toString() +
              item['name'].hashCode.toString(),
          name: item['name'] ?? 'Prodotto Sconosciuto',
          quantity: item['quantity'] ?? 1,
          category: item['category'] ?? 'Altro',
          isPantry: true,
          rawOcrName: item['name'], // Teniamo traccia del nome restituito
        ));
      }

      return extractedItems;
    } catch (e) {
      print("Errore in scanReceipt: $e");
      rethrow;
    }
  }
}
