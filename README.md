# <img src="https://github.com/user-attachments/assets/4a59fa72-0c5f-4caa-9983-aaf8a680222e" width="48"> WordView
**WordView** aims to be a language learning platform mostly centered around music, you can pick up an international song you like, listen to it and the app will create a full lesson around the lyrics!

# Development
Any help is welcome, if you think you can help with something, take a look at [good first issues](https://github.com/word-view/WordView-android/issues?q=is%3Aissue%20state%3Aopen%20label%3A%22good%20first%20issue%22) or the [WordView kanban](https://github.com/orgs/word-view/projects/2)

To start developing, you will need to have [APIWordView](https://github.com/word-view/APIWordView) running:
```sh
    # clone the repo
    git clone https://github.com/word-view/APIWordView

    # start the server
    cd ./APIWordView
    ./mvnw spring-boot:run
```
By default, the API will use the H2 database so changes are not persistent.
