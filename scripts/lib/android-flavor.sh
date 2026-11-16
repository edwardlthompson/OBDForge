# Shared flavor/APK path helpers. Source from other scripts.
# Env: OBDFORGE_FLAVOR=full|foss (default: full — GitHub/sideload identity)
OBDFORGE_FLAVOR="${OBDFORGE_FLAVOR:-full}"

android_flavor_cap() {
  local flavor="${1:-$OBDFORGE_FLAVOR}"
  echo "$(echo "${flavor:0:1}" | tr '[:lower:]' '[:upper:]')${flavor:1}"
}

android_assemble_release_task() {
  echo "assemble$(android_flavor_cap)Release"
}

android_release_apk_dir() {
  local android_root="$1"
  echo "$android_root/app/build/outputs/apk/${OBDFORGE_FLAVOR}/release"
}

android_find_release_apk() {
  local dir="$1"
  find "$dir" -name '*.apk' 2>/dev/null | head -1 || true
}
