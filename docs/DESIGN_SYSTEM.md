# Design System

> OBDForge visual contract. Read with `docs/DESIGN_GUIDE.md` and `modules/android/MODULE.md`.

## Baseline: Material 3 + Material You

- **Compose Material 3** via `GoldenPathTheme` (template baseline).
- **Material You** dynamic color from wallpaper/system on Android 12+.
- Typography and spacing from `design-tokens/design-tokens.json` → `ui/theme/`.
- Theme modes: system / light / dark (DataStore persisted).

## Garage themes (OBDForge extensions)

Product-specific palettes layered on M3 tokens for workshop readability:

| Theme | Persona default | Character |
|-------|-----------------|-----------|
| **Garage Classic** | Semi-pro | Neutral gray-blue; high legibility |
| **Garage Pro** | Shop | High contrast; amber accent for warnings |
| **Garage Night** | Racing | Dark-first; redline accent; minimal glare |

Implementation: extend `Color.kt` generated palette slots with `garage_*` semantic roles (warning, ok, live-pid, dtc-critical). Sync via `scripts/sync-design-tokens.py` after token edits.

## Semantic colors (diagnostics)

| Token | Use |
|-------|-----|
| `diagnostic_ok` | Normal PID range |
| `diagnostic_warn` | Borderline / pending readiness |
| `diagnostic_fault` | Active DTC, failed test |
| `diagnostic_live` | Streaming indicator |
| `diagnostic_interlock` | Blocked action, expert gate |

Never use raw hex in composables — reference `MaterialTheme.colorScheme` extensions or semantic aliases.

## M3 Expressive (future)

- Track **Material 3 Expressive** motion and shape roles for post-M12 polish sprint.
- Not blocking MVP; flag behind `PersonaPolicy` for Shop/Racing first.
- Expressive springs and emphasized cards for PID dashboards — prototype in `ui/expressive/` when API stable.

## Layout rules

- Max **250 lines** per view file; extract sub-composables early.
- Minimum **48.dp** touch targets for glove-friendly shop use.
- PID values use **tabular nums** (`FontFeatureSettings("tnum")`).
- RTL-safe: `Alignment.Start` / `End`; no hardcoded left/right padding.

## Strings and i18n

- All copy in `res/values/strings.xml`; keys prefixed by feature (`connect.*`, `live.*`, `shop.*`).
- English-only at launch; structure ready for `values-{lang}/`.

## FOSS constraints

- Allowed: `androidx.compose.material3`, `material-icons-extended`, `androidx.datastore`.
- Forbidden: Play Services, Firebase, proprietary icon fonts.

## Related docs

- Token workflow: `docs/DESIGN_GUIDE.md`
- Persona defaults: `docs/PERSONAS.md`
- ADR UI implications: `docs/adr/0004-persona-shop.md`
