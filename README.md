# ForkHub [![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=jp.forkhub) [![Build Status](https://travis-ci.org/jonan/ForkHub.svg?branch=master)](https://travis-ci.org/jonan/ForkHub)

The official [GitHub Android App](https://github.com/github/android/) hasn't been updated in a while and has been accumulating a few bugs and missing features, so I've decided to fork it and keep improving it.

I'll start by merging bug fixes that have been lying around for months, and once I'm comfortable again with the state of the app, I'd like to start adding new features.

You can see a comprehensive list of changes made to the original app in the [change log](https://github.com/jonan/ForkHub/blob/master/CHANGELOG.md).

[![Download from Google Play](https://cloud.githubusercontent.com/assets/3838734/3855877/4cf2a2dc-1eec-11e4-9634-2a1adf8f1c39.jpg)](https://play.google.com/store/apps/details?id=jp.forkhub)

Please see the [issues](https://github.com/jonan/ForkHub/issues) section to
report any bugs or feature requests and to see the list of known issues.

## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Building

### With Gradle

The easiest way to build is to install [Android Studio](https://developer.android.com/sdk/index.html) v1.+
with [Gradle](https://www.gradle.org/) v2.2.1.
Once installed, then you can import the project into Android Studio:

1. Open `File`
2. Import Project
3. Select `build.gradle` under the project directory
4. Click `OK`

Then, Gradle will do everything for you.

## Acknowledgements

This project uses the [GitHub Java API](https://github.com/eclipse/egit-github/tree/master/org.eclipse.egit.github.core)
built on top of [API v3](http://developer.github.com/).

It also uses many other open source libraries such as:

* [ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock)
* [CodeMirror](https://github.com/codemirror/CodeMirror)
* [RoboGuice](https://github.com/roboguice/roboguice)
* [ViewPagerIndicator](https://github.com/JakeWharton/Android-ViewPagerIndicator)

## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/jonan/ForkHub/pulls) to the `github` branch.
That way it will be easier to someday merge everything back to upstream.

Any contributions, large or small, major features, bug fixes, additional
language translations, unit/integration tests are welcomed and appreciated
but will be thoroughly reviewed and discussed.
