# Local AI — model selection

## Use case

OBDForge needs **short, persona-aware DTC explanations** entirely on-device (no cloud, F-Droid compatible). Typical output: 2–4 sentences plus one practical next step (~100–300 tokens).

## Requirements

| Requirement | Weight |
|-------------|--------|
| Offline / on-device | Required |
| FOSS-friendly runtime | Required |
| Instruction following | High |
| APK / repo size budget | High — default build uses OBDex catalog fallback |
| Latency on mid-range phone | Medium |
| Multimodal | Not needed |

## Options considered

| Model / stack | Pros | Cons | Verdict |
|---------------|------|------|---------|
| **Gemma 3 1B IT (INT4, MediaPipe `.task`)** | Smallest capable IT model; already integrated; litert-community build; ~530 MB download | MediaPipe LLM in maintenance mode; needs separate download for F-Droid | **Selected** |
| Gemma 2 2B IT | Better quality | ~2× size & RAM | Reject for default mobile target |
| Gemma 3n E2B / E4B | Newer family | Larger; overkill for DTC blurbs | Defer |
| LiteRT-LM migration | Google’s forward path | New API + `.litertlm` format; migration cost | Track for v1.2+ |
| Cloud APIs | Best quality | Violates local-first / F-Droid privacy story | Reject |

## Implementation

- **Runtime:** `com.google.mediapipe:tasks-genai` (`LlmInference`)
- **Model:** `litert-community/Gemma3-1B-IT` → `gemma3-1b-it-int4.task`
- **Fallback:** OBDex CC0 catalog (~9.5k codes) when model absent
- **Optional classifier:** `ai/dtc_classifier.tflite` (severity hook; not required)

### Developer bundle (optional)

```bash
python3 scripts/fetch-llm-model.py
# writes assets/ai/llm_model.task (gitignored)
```

### End-user download

DTC assistant offers **Download LLM** when the model is not bundled (~530 MB, Wi‑Fi recommended). Stored in app-private storage as `llm_model.task`.

### Configuration

See `LocalAiConfig` — 384 max output tokens, CPU backend, persona prompts in `LocalAiPolicy`.

## Licensing

- **Gemma 3:** [Gemma Terms of Use](https://ai.google.dev/gemma/terms) — accept on Hugging Face before download
- **MediaPipe:** Apache 2.0
- **OBDex catalog:** CC0-1.0

## Future

- Migrate to **LiteRT-LM** when stable for Gemma 3 1B `.litertlm` builds
- Optional Wal33D overlay for manufacturer-specific codes (separate from LLM)
