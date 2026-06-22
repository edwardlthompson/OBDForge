Optional on-device AI model assets for OBDForge (Sprint 12).

Place bundled models here for full local inference without network access:

- llm_model.task   MediaPipe LLM (.task format, e.g. Gemma 3 1B int4)
- dtc_classifier.tflite   Optional TFLite severity classifier hook

When models are absent, the app uses the offline DTC catalog fallback (CI and default F-Droid build).

See docs/EXECUTION_PLAN.md M12 for model size budget before shipping large assets.
