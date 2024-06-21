/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.starry.greenstash.ui.screens.settings.composables

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.starry.greenstash.BuildConfig
import com.starry.greenstash.R
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.weakHapticFeedback

sealed class AboutLinks(val url: String) {
    data object ReadMe : AboutLinks("https://github.com/Pool-Of-Tears/GreenStash")
    data object Website : AboutLinks("https://pooloftears.in")
    data object PrivacyPolicy :
        AboutLinks("https://github.com/Pool-Of-Tears/GreenStash/blob/main/legal/PRIVACY-POLICY.md")

    data object GithubIssues : AboutLinks("https://github.com/Pool-Of-Tears/GreenStash/issues")
    data object Telegram : AboutLinks("https://t.me/PotApps")
    data object Sponsor : AboutLinks("https://github.com/sponsors/starry-shivam")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.about_screen_header),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = greenstashFont
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        view.weakHapticFeedback()
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }) {
        LazyColumn(modifier = Modifier.padding(it)) {
            item {
                SettingsItem(title = stringResource(id = R.string.about_readme_title),
                    description = stringResource(id = R.string.about_readme_desc),
                    icon = Icons.AutoMirrored.Filled.Notes,
                    onClick = { Utils.openWebLink(context, AboutLinks.ReadMe.url) }
                )
            }
            item {
                SettingsItem(title = stringResource(id = R.string.about_website_title),
                    description = stringResource(id = R.string.about_website_desc),
                    icon = Icons.Filled.Web,
                    onClick = { Utils.openWebLink(context, AboutLinks.Website.url) }
                )
            }
            item {
                SettingsItem(title = stringResource(id = R.string.about_privacy_title),
                    description = stringResource(id = R.string.about_privacy_desc),
                    icon = Icons.Filled.PrivacyTip,
                    onClick = { Utils.openWebLink(context, AboutLinks.PrivacyPolicy.url) }
                )
            }
            item {
                SettingsItem(title = stringResource(id = R.string.about_gh_issue_title),
                    description = stringResource(id = R.string.about_gh_issue_desc),
                    icon = ImageVector.vectorResource(id = R.drawable.ic_about_gh_issue),
                    onClick = { Utils.openWebLink(context, AboutLinks.GithubIssues.url) }
                )
            }
            item {
                SettingsItem(title = stringResource(id = R.string.about_telegram_title),
                    description = stringResource(id = R.string.about_telegram_desc),
                    icon = ImageVector.vectorResource(id = R.drawable.ic_about_telegram),
                    onClick = { Utils.openWebLink(context, AboutLinks.Telegram.url) }
                )
            }
            item {
                SettingsItem(title = stringResource(id = R.string.about_support_title),
                    description = stringResource(id = R.string.about_support_desc),
                    icon = Icons.Filled.Favorite,
                    onClick = { Utils.openWebLink(context, AboutLinks.Sponsor.url) }
                )
            }
            item {
                SettingsItem(title = stringResource(id = R.string.about_version_title),
                    description = stringResource(id = R.string.about_version_desc).format(
                        BuildConfig.VERSION_NAME
                    ),
                    icon = Icons.Filled.Info,
                    onClick = { clipboardManager.setText(AnnotatedString(getVersionReport())) }
                )
            }
        }
    }
}


fun getVersionReport(): String {
    val versionName = BuildConfig.VERSION_NAME
    val versionCode = BuildConfig.VERSION_CODE
    val release = if (Build.VERSION.SDK_INT >= 30) {
        Build.VERSION.RELEASE_OR_CODENAME
    } else {
        Build.VERSION.RELEASE
    }
    return StringBuilder().append("App version: $versionName ($versionCode)\n")
        .append("Android Version: Android $release (API ${Build.VERSION.SDK_INT})\n")
        .append("Device information: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})\n")
        .append("Supported ABIs: ${Build.SUPPORTED_ABIS.contentToString()}\n")
        .toString()
}

@Preview
@Composable
fun AboutScreenPV() {
    AboutScreen(navController = rememberNavController())
}
