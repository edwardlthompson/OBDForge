#!/usr/bin/env python3
"""Export 512x512 store/README launcher PNG (matches ic_launcher_foreground.xml geometry)."""
from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
SIZE = 512
BG = (11, 15, 20)
ENGINE = (255, 51, 51)
PORT = (255, 255, 255)
SCALE = SIZE / 108.0
STROKE = max(8, int(round(3.2 * SCALE)))

OUT_PATHS = [
    ROOT / "docs" / "assets" / "icon.png",
    ROOT / "examples" / "android" / "metadata" / "en-US" / "images" / "icon.png",
]


def s(x: float, y: float) -> tuple[float, float]:
    return x * SCALE, y * SCALE


def engine_outline() -> list[tuple[float, float]]:
    """Standard MIL engine silhouette — orthogonal segments only."""
    return [
        s(35, 26),
        s(35, 30),
        s(31, 30),
        s(31, 22),
        s(43, 22),
        s(43, 26),
        s(69, 26),
        s(69, 40),
        s(65, 40),
        s(65, 44),
        s(59, 44),
        s(59, 48),
        s(49, 48),
        s(49, 44),
        s(45, 44),
        s(45, 40),
        s(41, 40),
        s(41, 30),
        s(35, 30),
    ]


def port_polygon() -> list[tuple[float, float]]:
    return [s(50, 56), s(58, 56), s(76, 59), s(82, 86), s(26, 86), s(32, 59)]


def pin_rects() -> list[tuple[float, float, float, float]]:
    rects: list[tuple[float, float, float, float]] = []
    for row_y in (67, 76):
        for col in range(8):
            x = 34 + col * 5
            rects.append((*s(x, row_y), *s(x + 3, row_y + 5)))
    return rects


def render() -> Image.Image:
    img = Image.new("RGB", (SIZE, SIZE), BG)
    draw = ImageDraw.Draw(img)
    outline = engine_outline()
    draw.line(outline + [outline[0]], fill=ENGINE, width=STROKE, joint="curve")
    draw.polygon(port_polygon(), fill=PORT)
    for x0, y0, x1, y1 in pin_rects():
        draw.rectangle((x0, y0, x1, y1), fill=BG)
    return img


def main() -> None:
    img = render()
    for out in OUT_PATHS:
        out.parent.mkdir(parents=True, exist_ok=True)
        img.save(out, format="PNG", optimize=True)
        kb = out.stat().st_size // 1024
        print(f"OK   {out} ({SIZE}x{SIZE}, {kb} KB)")


if __name__ == "__main__":
    main()
