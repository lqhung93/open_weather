Author: Hung Le

Email: lqhung93@gmail.com

# Weather Forecast

## Acknowledgement
- Use Kotlin
- Use MVVM for architecture, data binding and Fragment Navigation component.
- Use Retrofit and OkHttp for calling REST API.
- Use Rx Java for network event process and caching handler.
- Use Shared Preferences for API caching.
- JUnit, Espresso.
- Store API key to external file for security.
- Make Encryption utilities for shared preferences to save AppId, caching data.
- Git model: https://nvie.com/posts/a-successful-git-branching-model/

## Folder structure
- adapters: for recycler view adapter
- api: for retrofit services
- base: for base classes, BaseActivity, BaseFragment
- data: for caching logic
- fragments: for Fragment classes
- models: for API and data models classes
- repository: for Repository class
- utils: for Utilities classes, AppId handler, Encryption, Date, Shared Preferences
- viewholders: for recycler View holder classes
- viewmodels: for View model classes

## Libraries
- Android X
- Fragment Navigation
- Retrofit
- Gson
- OKHttp
- Rx Java
- Mockito
- Powermock
- Espresso
- JUnit
- Fragment Testing

## How to build
- Download source code from Github or pull from `master` branch.
- Open in Android Studio.

## Features
- The application is a simple Android application which is written byJava/Kotlin.
- The application is able to retrieve the weather information from Open Weather Maps API.
- The application is able to allow user to input the searching term.
- The application is able to proceed searching with a condition of the search term length
must be from 3 characters or above.
- The application is able to render the searched results as a list of weather items.
- The application is able to support caching mechanism so as to prevent the app from
generating a bunch of API requests.
- The application is able to manage caching mechanism & lifecycle.
- The application is able to handle failures.