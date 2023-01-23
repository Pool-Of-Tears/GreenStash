<p align="center">
  <a href=""><img width="200" height="200" src="https://github.com/Pool-Of-Tears/GreenStash/blob/master-old/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png"></a>
</p>
<p align="center" style="font-size:18px"><b>GreenStash</b></p>

<p align="center">
  <a href="https://www.android.com"><img src="https://forthebadge.com/images/badges/built-for-android.svg"></a> <a href="https://www.github.com/starry69"><img src="https://forthebadge.com/images/badges/built-with-love.svg"/></a>
</p>

**GreenStash** is a simple [FOSS](https://en.m.wikipedia.org/wiki/Free_and_open-source_software) android app to help you plan and manage your savings goals easily and establish the habit of saving money.

------

<b>⚠️ Importand Note:</b> <i>The app is currently being rewritten in jetpack compose, the next release (v2.0) may not be compatible with older releases (i.e you will need to uninstall older version to install this one) as I'm planning to do a few things differently the way they were before. For source code of last and previous releases please checkout <code>master-old</code> branch instead.</i>.

------

### 👇 Downloads
<a href="https://f-droid.org/en/packages/com.starry.greenstash"><img alt="Get it on F-Droid" height="65" src="https://f-droid.org/badge/get-it-on.png" /></a>
<a href='https://apt.izzysoft.de/fdroid/index/apk/com.starry.greenstash'><img alt='Get it on IzzyOnDroid' src='https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png' height='65'/></a>
[<img src="https://github.com/machiav3lli/oandbackupx/blob/034b226cea5c1b30eb4f6a6f313e4dadcbb0ece4/badge_github.png" alt="Get it on GitHub" height="65">](https://github.com/Pool-Of-Tears/GreenStash/releases/latest)

Please keep in mind that you cannot update F-droid builds over any other builds due to signature mismatch. (F-droid builds are signed by F-droid's own keys while others are signed with my own private key) if you wish to migrate over F-droid releases you can take backup of app data and restore it after installing F-droid version.

### 📸 Screenshots
| ![](https://telegra.ph/file/5639878ff67f6aedc103d.png) | ![](https://telegra.ph/file/66ff44ea0c6678d81acff.png) | ![](https://telegra.ph/file/77d146c3d1450ab0214a2.png) | ![](https://telegra.ph/file/0eca162e0a15324b8bd64.png) |
|--------------------------------------------------------|--------------------------------------------------------|--------------------------------------------------------|--------------------------------------------------------|
| ![](https://telegra.ph/file/4866da8a08c88f095d3c7.png) | ![](https://telegra.ph/file/1119d0f22b510167818eb.png) | ![](https://telegra.ph/file/aae688eaee7f130230750.png) | ![](https://telegra.ph/file/6230df331b84e09332117.png) |

### ✨ Features
- Clean & beautiful UI based on Google's [material design three](https://m3.material.io/) guidelines.
- Add images to your saving goals to keep you motivated!
- View how much you need to save daily/weekly/monthly to achieve your goal before deadline.
- View detailed transaction (withdraw/deposit) history.
- Supports around 100+ local currency symbols.
- Inbuilt biometric app lock to keep your financial data safe and secure.
- Fully offline, greenstash doens't require internet permission to work.
- Compatible with Android 7.0 and above (API 24+)
- Supports [Material You](https://www.androidpolice.com/everything-we-love-about-material-you/amp/) theming in devices running on Android 12+
- Comes in both light and dark mode.

### 🙌 Translations
If you want to make app available in your language, you're very welcome to create a [pull request](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests) with your translation file.
The string resources can be found under `/app/src/main/res/values/strings.xml`. It is easiest to make a translation using the Android Studio XML editor, but you can always go with your favorite XML-text editor instead.
Checkout this guide to learn more about translation strings from [Helpshift](https://developers.helpshift.com/android/i18n/) for Android.

### ♥️ Donations
If this project helped you a little bit, please consider donating a small amount to support further development and ofcourse boosting morale :)

[![Github-sponsors](https://img.shields.io/badge/sponsor-30363D?style=for-the-badge&logo=GitHub-Sponsors&logoColor=#EA4AAA)](https://github.com/sponsors/starry-shivam)
[![Bitcoin](https://img.shields.io/badge/Bitcoin-000?style=for-the-badge&logo=bitcoin&logoColor=white)](https://www.blockchain.com/btc/address/bc1q82qh9hw5xupwlf0f3ddfud63sek53lavk6cf0k)
[![Ethereum](https://img.shields.io/badge/Ethereum-3C3C3D?style=for-the-badge&logo=Ethereum&logoColor=white)](https://www.blockchain.com/eth/address/0x9ef20ad6FBf1985e6eF6ea6337ad800Cb8126eD3)
![](https://img.shields.io/badge/starry%40airtel-UPI-red?style=for-the-badge)

### 🛠️ Built with
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - To improve performance and overall user experience.
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
    - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - Data objects that notify views when the underlying database changes.
    - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes.
    - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - Generates a binding class for each XML layout file present in that module and allows you to more easily write code that interacts with views.
    - [Jetpack navigation](https://developer.android.com/guide/navigation) - Navigation component helps you implement navigation, from simple button clicks to more complex patterns, such as app bars and the navigation drawer.
- [Dagger-Hilt](https://dagger.dev/hilt/) For [Dependency injection (DI)](https://developer.android.com/training/dependency-injection)
- [Room database](https://developer.android.com/jetpack/androidx/releases/room) - Persistence library provides an abstraction layer over SQLite to allow for more robust database access while harnessing the full power of SQLite.
- [Material Components for Android](https://github.com/material-components/material-components-android) - Modular and customizable Material Design UI components for Android.
- [ImagePicker](https://github.com/Dhaval2404/ImagePicker) - Image Picker for Android, Pick an image from Gallery or Capture a new image with Camera.

### ©️ Licence
[MIT][license] © [Stɑrry Shivɑm][github]

[license]: /LICENSE
[github]: https://github.com/starry-shivam
```
MIT License

Copyright (c) 2022 Stɑrry Shivɑm

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
