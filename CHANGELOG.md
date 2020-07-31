# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.7-BETA] - 2020-07-31
### Added
- Small REST API for retrieving the bot's status
- Minor metrics system using Micrometer and Prometheus
- ClearQueue command, for, well, clearing the queue
- Custom banner

## [1.6.2-BETA] - 2020-07-26
### Changed
- Slight language update, queue now says "Who put it on?" instead of "Requested By:"

### Fixed
- Bug with hour time formatting, was multiplying the time in minutes by 3600 instead of 60 and taking that away
  from the time in seconds (to calculate time in seconds), producing a negative second count every time


## [1.6.1-BETA] - 2020-07-25
### Added
- A Docker file for creating a Docker image, for bot to be ran in a Docker container
- You can now define multiple lava link nodes
- README for self-hosters, and just for a nice landing page for project viewers
- This changelog file that you're currently viewing!

### Changed
- Old method of providing command-line arguments has been removed and replaced with an external
  application.yml file for configuration
- Naming and constructors, specifically the removal of the redundant @Autowired constructor on Spring components
- Possible slight performance increase (untested) from setting member cache policy and chunking filter
- All occurrences of @Configuration have been removed and replaced with @ConstructorBinding
- All configuration classes have been changed to data classes to clean them up, and also because @ConstructorBinding
  allows values to be bound by the constructor, so it only made sense really
- Made sentry optional

### Fixed
- Bug where bot was not automatically disconnecting after five minutes (like it is set up to do)

### Removed
- logback-debug.xml file, was not necessary to be included