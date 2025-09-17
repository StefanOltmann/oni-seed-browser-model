# ONI Seed Browser object model

![Kotlin](https://img.shields.io/badge/kotlin-2.2.20-blue.svg?logo=kotlin)
[![GitHub Sponsors](https://img.shields.io/badge/Sponsor-gray?&logo=GitHub-Sponsors&logoColor=EA4AAA)](https://github.com/sponsors/StefanOltmann)

This is the object model for https://github.com/StefanOltmann/oni-seed-browser as a KMP library.

It's a representation of a game map of the game [Oxygen Not Included](https://www.klei.com/games/oxygen-not-included).

## Installation

It's available on Maven Central Snapshot Repository.

Add the snapshot repository to your `build.gradle.kts` file:
```
repositories {
    mavenCentral()
    maven(url = "https://central.sonatype.com/repository/maven-snapshots/")
}
```

Then add this to the dependencies (where `d35090d` is to be replaced with the latest commit hash):
```
implementation("de.stefan-oltmann:oni-seed-browser-model:d35090d-SNAPSHOT")
```

## Contributions

Contributions to this project are welcome! If you encounter any issues,
have suggestions for improvements, or would like to contribute new features,
please feel free to submit a pull request.

## License

ONI Seed Browser is licensed under the GNU Affero General Public License (AGPL),
ensuring the community's freedom to use, modify, and distribute the software.
