# Scan Manager

Basic open-source scan manager for photo archivists.

The goal is to create a useful, general-purpose tool that helps with scan file management, can keep track of
progress, and can help fix problems caused by poor historical project management.

## Feature List

- [x] Keep track of scanning progress, including binders, sets, and set data.
  - Set data includes numbers, batches, and descriptions — everything already kept track of.
- [ ] Analyze existing scan data, importing, comparing, and reorganising files as needed.
  - Import existing scans, compare original and edited files, and re-export originals as needed.
  - Import existing tracking data from Markdown tracking files.
- [ ] Store data in a format that users can move between devices easily.
- [ ] Provide a relatively intuitive user interface.
- [ ] Potentially show a floating window with the current binder/set/processing status.
- [ ] Provide an easily accessed new user tutorial.

## Research

- ImageIO extensions for loading PSD files: https://github.com/haraldk/TwelveMonkeys
- Image comparison library that highlights differences: https://romankh3.github.io/image-comparison/
- Perceptual hashing library for images: https://github.com/KilianB/JImageHash
- H2 database library: https://github.com/h2database/h2database
