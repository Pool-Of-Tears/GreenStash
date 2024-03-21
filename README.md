<p align="center">
  <a href=""><img width="200" height="200" src="https://github.com/Pool-Of-Tears/GreenStash/blob/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png"></a>
</p>
<h1 align="center">GreenStash</h1>

<p align="center">
  <a href="https://www.android.com"><img src="https://forthebadge.com/images/badges/built-for-android.svg"></a> <a href="https://www.github.com/starry69"><img src="https://forthebadge.com/images/badges/built-with-love.svg"/></a>
</p>

<p align="center">
  <a href="https://t.me/PotApps"><img src="https://img.shields.io/badge/Telegram-PotApps-green?style=flat&logo=telegram"/></a>
  <a href="https://github.com/Pool-Of-Tears/GreenStash/releases"><img src="https://img.shields.io/github/downloads/Pool-Of-Tears/GreenStash/total?label=Downloads&logo=github"</img></a>
  <img alt="GitHub" src="https://img.shields.io/github/license/Pool-Of-Tears/GreenStash">
  <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/Pool-Of-Tears/GreenStash">
  <a href="https://www.repostatus.org/#active"><img src="https://www.repostatus.org/badges/latest/active.svg" alt="Project Status: Active – The project has reached a stable, usable state and is being actively developed." /></a>
  <img alt="build-workflow"src="https://github.com/Pool-Of-Tears/GreenStash/actions/workflows/android.yml/badge.svg">
</p>

------

**GreenStash** is a simple [FOSS](https://en.m.wikipedia.org/wiki/Free_and_open-source_software) android app to help you plan and manage your savings goals easily and establish the habit of saving money.

------

<h2 align="center">Screenshots</h2>

| ![](https://te.legra.ph/file/62e0a71bfbba51aa64f44.png) | ![](https://te.legra.ph/file/396f07eebc3cacae8da48.png) | ![](https://te.legra.ph/file/9754841ed1b6f9c4bdb89.png) |
|-------------------------------------------------------|-------|-------------------------------------------------------|
| ![](https://te.legra.ph/file/9e135107db6a8d3acc758.png) | ![](https://te.legra.ph/file/8fae219ae0dfc8d5ae178.png) | ![](https://te.legra.ph/file/12596309941afd16cf5a6.png) |

------

<h2 align="center">Highlights</h2>

- Clean & beautiful UI based on Google's [material design three](https://m3.material.io/) guidelines.
- Add images to your saving goals to keep you motivated!
- View how much you need to save daily/weekly/monthly to achieve your goal before deadline.
- View detailed transaction (withdraw/deposit) history.
- Get daily, semi-weekly or weekly reminders for your saving goals based on goal priority. 
- Supports around 100+ local currency symbols.
- Inbuilt biometric app lock to keep your financial data safe and secure.
- Fully offline, greenstash doens't require internet permission to work.
- Compatible with Android 7.0 and above (API 24+)
- Supports [Material You](https://www.androidpolice.com/everything-we-love-about-material-you/amp/) theming in devices running on Android 12+
- MAD: UI and logic written with pure Kotlin. Single activity, no fragments, only composable destinations.

------

<h2 align="center">Downloads</h2>

<div align="center">
<a href="https://play.google.com/store/apps/details?id=com.starry.greenstash"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" height="65"</img></a>
<a href='https://f-droid.org/packages/com.starry.greenstash/'><img alt='Get it on F-Droid' src='https://fdroid.gitlab.io/artwork/badge/get-it-on.png' height='65'/></a>
<a href='https://apt.izzysoft.de/fdroid/index/apk/com.starry.greenstash'><img alt='Get it on IzzyOnDroid' src='https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png' height='65'/></a>
<a href="https://github.com/Pool-Of-Tears/GreenStash/releases/latest"><img alt="Get it on GitHub" src="https://github.com/machiav3lli/oandbackupx/blob/034b226cea5c1b30eb4f6a6f313e4dadcbb0ece4/badge_github.png" height="65"</img></a>
</div>

------

<h2 align="center">Donations</h2>

If this project helped you a little bit, please consider donating a small amount to keep the projects going, Your donations helps ensure that my open source projects are actively maintained. You can donate via following mediums:

[![Github-sponsors](https://img.shields.io/badge/sponsor-30363D?style=for-the-badge&logo=GitHub-Sponsors&logoColor=#EA4AAA)](https://github.com/sponsors/starry-shivam)
[![Bitcoin](https://img.shields.io/badge/Bitcoin-000?style=for-the-badge&logo=bitcoin&logoColor=white)](https://www.blockchain.com/btc/address/bc1q82qh9hw5xupwlf0f3ddfud63sek53lavk6cf0k)
![](https://img.shields.io/badge/starry%40airtel-UPI-red?style=for-the-badge)

Or you could just buy the app from Google Play Store. You'll get constant updates without the need to get all the way here and download the release all by yourself and install the release all by yourself and then you realize you have a different package name so you contact me but I really don't have time for this so I update it on google play and then you realize your installer doesn't work and then you realize your whole life is like this, miserable and not working but then you see a point of light in the distance so you run towards and it's the "Buy" button in the GreenStash's page on google play, it'll make your life easier.

------

<h2 align="center">Translations</h2>

If you want to make app available in your language, you're very welcome to create a [pull request](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests) with your translation file.
The string resources can be found under `/app/src/main/res/values/strings.xml`. It is easiest to make a translation using the Android Studio XML editor, but you can always go with your favorite XML-text editor instead.
Checkout this guide to learn more about translation strings from [Helpshift](https://developers.helpshift.com/android/i18n/) for Android.

------

<h2 align="center">Tech Stack</h2>

- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - To improve performance by doing I/O tasks out of main thread asynchronously.
- [Flow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) - A cold asynchronous data stream that sequentially emits values and completes normally or with an exception.
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [Jetpack Compose](https://developer.android.com/jetpack/compose?gclsrc=ds&gclsrc=ds) - Jetpack Compose is Android’s recommended modern toolkit for building native UI
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - Data objects that notify views when the underlying database changes.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes.
- [Lottie](https://airbnb.design/lottie) - Lottie is an Android, iOS and React Native library that renders After Effects animations in real time.
- [Coil](https://coil-kt.github.io/coil/compose) - An image loading library for Android backed by Kotlin Coroutines.
- [Dagger-Hilt](https://dagger.dev/hilt) For [Dependency injection (DI)](https://developer.android.com/training/dependency-injection)
- [Room database](https://developer.android.com/jetpack/androidx/releases/room) - Persistence library provides an abstraction layer over SQLite to allow for more robust database access while harnessing the full power of SQLite.

------

<h2 align="center">License</h2>

[MIT License][license] © [Stɑrry Shivɑm][github]

[license]: /LICENSE
[github]: https://github.com/starry69

```
MIT License

Copyright (c) [2022 - Present] Stɑrry Shivɑm

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
