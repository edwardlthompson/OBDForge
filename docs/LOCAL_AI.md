# Local AI — model selection

## Use case

OBDForge needs **short, persona-aware DTC explanations** entirely on-device (no cloud). Typical output: 2–4 sentences plus one practical next step (~100–300 tokens).

## Product flavors

| Flavor | Who ships it | On-device LLM |
|--------|----------------|---------------|
| **`foss`** | F-Droid (`assembleFossRelease`) | Not included. Catalog explanations only, with an in-app note. |
| **`full`** | GitHub Releases / sideload (`assembleFullRelease`, default) | Optional Gemma 3 1B IT download via MediaPipe GenAI. |
`foss` has no `com.google.mediapipe:tasks-genai` dependency and never downloads from Hugging Face.

## Requirements

| Requirement | Weight |
|-------------|--------|
| Offline / on-device | Required |
| FOSS-friendly runtime | Required for `foss` |
| Instruction following | High |
| APK / repo size budget | High — default build uses OBDex catalog fallback |
| Latency on mid-range phone | Medium |
| Multimodal | Not needed |
## Options considered

| Model / stack | Pros | Cons | Verdict |
|---------------|------|------|---------|
| **Gemma 3 1B IT (INT4, MediaPipe `.task`)** | Smallest capable IT model; already integrated; litert-community build; ~530 MB download | Gemma Terms of Use; not F-Droid eligible | **`full` flavor only** |
| Gemma 2 2B IT | Better quality | ~2× size & RAM | Reject for default mobile target |
| Gemma 3n E2B / E4B | Newer family | Larger; overkill for DTC blurbs | Defer |
| LiteRT-LM migration | Google’s forward path | New API + `.litertlm` format; still Gemma-licensed | Track for `full` |
| llama.cpp + user-picked openly licensed GGUF | F-Droid-friendly if the runtime and model are free | Not implemented | Proposed follow-up, not in this tree |
| Cloud APIs | Best quality | Violates local-first / F-Droid privacy story | Reject |
## Implementation

- **`full` runtime:** `com.google.mediapipe:tasks-genai` (`LlmInference`)
- **`full` model:** `litert-community/Gemma3-1B-IT` → `gemma3-1b-it-int4.task`
- **Both flavors:** OBDex CC0 catalog (~9.5k codes) when the LLM is absent
- **Optional classifier:** `ai/dtc_classifier.tflite` (Apache-2.0 TensorFlow Lite; not required)

### Developer bundle (`full` only)

```bash
python3 scripts/fetch-llm-model.py
# writes src/full/assets/ai/llm_model.task (gitignored)

```

### End-user download (`full` only)

DTC assistant offers **Download LLM** when the model is not bundled (~530 MB, Wi‑Fi recommended). Stored in app-private storage as `llm_model.task`.

### Configuration

See `LocalAiConfig` in the `full` source set — 384 max output tokens, CPU backend, persona prompts in `LocalAiPolicy`.

## Licensing

- **Gemma 3:** [Gemma Terms of Use](https://ai.google.dev/gemma/terms) — `full` flavor only; accept on Hugging Face before download
- **MediaPipe:** Apache 2.0 (`fullImplementation`)
- **OBDex catalog:** CC0-1.0
- **TensorFlow Lite classifier:** Apache 2.0

## Future

- Optional Wal33D overlay for manufacturer-specific codes (separate from LLM)
- If a FOSS on-device LLM ships later, it belongs in `foss` (for example llama.cpp plus a user-chosen openly licensed GGUF)
