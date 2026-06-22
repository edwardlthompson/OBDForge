Optional on-device AI model assets for OBDForge (Sprint 12).

Selected model: Gemma 3 1B IT INT4 (MediaPipe .task) — see docs/LOCAL_AI.md

- llm_model.task   Gemma 3 1B IT INT4 (~530 MB). Fetch: python3 scripts/fetch-llm-model.py
- dtc_classifier.tflite   Optional TFLite severity classifier hook

When models are absent, the app uses the OBDex offline DTC catalog. Users can download
the LLM from the DTC assistant screen (Wi‑Fi recommended).
