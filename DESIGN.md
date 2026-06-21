---
name: Kinship & Co
colors:
  surface: '#f7f9fb'
  surface-dim: '#d8dadc'
  surface-bright: '#f7f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f6'
  surface-container: '#eceef0'
  surface-container-high: '#e6e8ea'
  surface-container-highest: '#e0e3e5'
  on-surface: '#191c1e'
  on-surface-variant: '#44474c'
  inverse-surface: '#2d3133'
  inverse-on-surface: '#eff1f3'
  outline: '#75777d'
  outline-variant: '#c4c6cd'
  surface-tint: '#515f74'
  primary: '#303e51'
  on-primary: '#ffffff'
  primary-container: '#475569'
  on-primary-container: '#bbcae1'
  inverse-primary: '#b9c7df'
  secondary: '#516072'
  on-secondary: '#ffffff'
  secondary-container: '#d2e1f7'
  on-secondary-container: '#556477'
  tertiary: '#343e47'
  on-tertiary: '#ffffff'
  tertiary-container: '#4b555f'
  on-tertiary-container: '#bfc9d5'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d5e3fc'
  primary-fixed-dim: '#b9c7df'
  on-primary-fixed: '#0d1c2e'
  on-primary-fixed-variant: '#3a485b'
  secondary-fixed: '#d4e4fa'
  secondary-fixed-dim: '#b9c8de'
  on-secondary-fixed: '#0d1c2d'
  on-secondary-fixed-variant: '#39485a'
  tertiary-fixed: '#dae3f0'
  tertiary-fixed-dim: '#bdc8d3'
  on-tertiary-fixed: '#131d25'
  on-tertiary-fixed-variant: '#3e4852'
  background: '#f7f9fb'
  on-background: '#191c1e'
  surface-variant: '#e0e3e5'
typography:
  headline-xl:
    fontFamily: Plus Jakarta Sans
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  xs: 4px
  sm: 12px
  md: 24px
  lg: 48px
  xl: 80px
  gutter: 24px
  margin-mobile: 16px
  margin-desktop: 64px
---

## Brand & Style

The brand identity centers on "Calm Sophistication." It transitions from a high-energy, sunny aesthetic to a refined, tranquil experience designed for modern connection. The target audience values intentionality, minimalism, and a clutter-free mental space.

This design system employs a **Minimalist** style infused with **Soft Tonal Layering**. The visual narrative is quiet and premium, prioritizing negative space and a restrained color application to evoke a sense of professional serenity. The UI should feel airy and breathable, moving away from high-saturation triggers toward a more architectural and composed interface.

## Colors

The palette is built on a foundation of **Muted Slate Gray** (#475569) as the primary anchor, providing a grounded and professional tone. Surfaces utilize **Off-White** (#F8FAFC) and **Light Gray** (#F1F5F9) to create a soft, non-reflective canvas that reduces eye strain.

Accents are strictly desaturated and used sparingly for subtle differentiation rather than high-contrast calls to action. These include:
- **Dusty Blue**: For secondary information and quiet links.
- **Sage Green**: For positive states and organic growth metaphors.
- **Mauve**: For tertiary highlights and soft alerts.

Avoid pure black (#000000). Use the primary slate gray for all text to maintain a low-contrast, sophisticated legibility.

## Typography

This design system exclusively uses **Plus Jakarta Sans** to maintain a modern, friendly, yet professional character. The typeface's wide apertures and clean geometry support the minimalist aesthetic.

Headlines should use tighter letter spacing and heavier weights to provide structural hierarchy against the muted color palette. Body text is optimized for readability with generous line heights. Labels should occasionally use uppercase styling with slight letter spacing to act as "structural signposts" within the layout without needing heavy color fills.

## Layout & Spacing

The layout follows a **Fluid Grid** model based on an 8px square rhythm. 

- **Desktop**: 12-column grid with 24px gutters and 64px side margins.
- **Tablet**: 8-column grid with 20px gutters and 32px side margins.
- **Mobile**: 4-column grid with 16px gutters and 16px side margins.

Content should be grouped using generous whitespace (the "Large" and "Extra Large" spacing units) to enforce the "Calm" brand pillar. Avoid crowding elements; if a section feels full, increase the padding to allow the eye to rest.

## Elevation & Depth

Depth is conveyed through **Tonal Layers** and **Low-contrast Outlines** rather than traditional shadows. 

1.  **Base Layer**: Off-white background.
2.  **Mid Layer**: Light gray containers (#F1F5F9) used for grouping content.
3.  **Top Layer**: White cards with a 1px solid stroke in Tertiary Slate (#CBD5E1).

Shadows, if used at all, must be "Ambient Shadows": extremely diffused (20-40px blur), low opacity (3-5%), and tinted with the Primary Slate color to ensure they feel like part of the environment rather than a floating object.

## Shapes

In alignment with the "Round Eight" specification, the design system utilizes a **Rounded** shape language.

- **Standard Elements**: 0.5rem (8px) radius for buttons, inputs, and small widgets.
- **Large Elements**: 1rem (16px) radius for cards and modal containers.
- **Extra Large Elements**: 1.5rem (24px) radius for hero sections or distinct background containers.

The consistency of the 8px base radius provides a friendly, approachable feel that softens the "coldness" of the slate-gray palette.

## Components

### Buttons
- **Primary**: Solid Slate Gray (#475569) with white text. 8px corner radius.
- **Secondary**: Ghost style with 1px border in Slate Gray and Slate Gray text.
- **Tertiary**: Subtle Light Gray fill with Slate Gray text, no border.

### Input Fields
Inputs should be minimal. Use a 1px border in Light Gray (#F1F5F9) that transitions to Slate Gray on focus. Labels should be small and placed above the field in a Medium weight.

### Cards
Cards are the primary container. Use a white background, an 8px or 16px corner radius, and a 1px border (#F1F5F9). Do not use heavy drop shadows.

### Chips & Accents
Use the pastel accent colors (Sage, Mauve, Dusty Blue) for chips. Chips should have a background opacity of 15% of the accent color and text of the full-strength accent color to keep them readable but muted.

### Lists
Lists should have generous vertical padding (16px) between items with a very thin, light gray separator line that does not touch the edges of the container.