# Change Log

## [Unreleased]

### Fixed
- Some code formats where not being properly colored.
- Update all icons (Octicons v3.2.0).
- Don't show references to nonexistent commits in issues.
- If an issue is closed from a commit, show the commit in the event.
- Fix bug with files with same name but different case.
- Some minor bug fixes.

## [ForkHub v1.1.0] - 2015-11-06

### Added
- Lots of visual improvements, including basic Material Design.
- Improve information shown in Pull Requests.
- Open Gist links containing the owner's login.
- New button to report an issue directly to ForkHub.
- Use OkHttp as HTTP client.

### Fixed
- Properly detect all markup formats.
- Froyo is supported again.
- Some minor bug fixes.

## [ForkHub v1.0.0] - 2015-09-18

### Fixed
- Fix two-factor authentication.
- Fix loading annotated tags.
- Only show valid branches and tags in the select dialog.

## [ForkHub v0.9.2] - 2015-08-20

### Fixed
- Creating or editing a comment could crash the app on old Android versions.
- Improve the source viewer for many languages (CodeMirror 4.13.0).
- Update all icons (Octicons v3.1.0).

### Translations
- Improve translations: KO.

## [ForkHub v0.9.1] - 2015-07-22

### Fixed
- Don't show the starred repos of the registered user in the repository list.

## [ForkHub v0.9.0] - 2015-05-27

### Fixed
- Fix bug that didn't allow you to see issues if you are not the maintainer of the repo.
- You can now edit issues you've created in third party repos.
- Improve image loading to save memory. This fixes a lot of 'Out of memory' crashes.
- Use Gradle instead of Maven for compilation.
- Improve how github.com links are manage and add support for opening new types of links.
- Improve the source viewer for many languages (CodeMirror 4.8.0).
- Fix duplicated issues when searching.
- Update all icons (Octicons v2.2.0).

### Added
- Add forking support.
- Add support for editing and deleting issue comments.
- Add support for copying the hash of a commit.
- Add new swipe-to-refresh animation.
- Add event information to issues.

### Translations
- New translations: CS, IS.
- Improve translations: DE, ES, EL, IT, IW, JA, KO, RU, SK, SV, TR.

## [1.9.0] - 2014-02-21

- Last official GitHub release

[Unreleased]: https://github.com/jonan/ForkHub/compare/ForkHub-v1.1.0...master
[ForkHub v1.1.0]: https://github.com/jonan/ForkHub/compare/ForkHub-v1.0.0...ForkHub-v1.1.0
[ForkHub v1.0.0]: https://github.com/jonan/ForkHub/compare/ForkHub-v0.9.2...ForkHub-v1.0.0
[ForkHub v0.9.2]: https://github.com/jonan/ForkHub/compare/ForkHub-v0.9.1...ForkHub-v0.9.2
[ForkHub v0.9.1]: https://github.com/jonan/ForkHub/compare/ForkHub-v0.9.0...ForkHub-v0.9.1
[ForkHub v0.9.0]: https://github.com/jonan/ForkHub/compare/1.9.0...ForkHub-v0.9.0
[1.9.0]: https://github.com/jonan/ForkHub/releases/tag/1.9.0
