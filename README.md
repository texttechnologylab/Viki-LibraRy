<div align="center">
  <img src="https://github.com/texttechnologylab/Viki-LibraRy/assets/49918134/1fde9c36-6c27-4260-a4b4-2339fef2925f" width="500"/>
  <h3>A virtual reality library for collaborative browsing and navigation through hypertext</h3> 
  <hr />
</div>

[![Conference](http://img.shields.io/badge/conference-HT--2023-4b44ce.svg)](https://ht.acm.org/ht2023/)
[![version](https://img.shields.io/github/license/texttechnologylab/Viki-LibraRy)]()
[![latest](https://img.shields.io/github/v/release/texttechnologylab/Viki-LibraRy)]()

# Abstract

*Viki LibraRy*, is a first implementation for generating and exploring online information based on hypertext systems in a three-dimensional environment using virtual reality. Thereby a virtual library, based on Wikipedia, is created, in which Rooms are dynamically created with data, which is provided via a RESTful backend. In these Rooms the user can browse through all kind of different articles of the category in the form of Books. In addition, users can access different Rooms, through virtual portals. Beyond that, the explorations can be done alone or collaboratively, using Ubiq.

# In medias res
Viki LibraRy in action             |  Room under construction
:-------------------------:|:-------------------------:
![image](https://github.com/texttechnologylab/Viki-LibraRy/assets/49918134/f1b978cf-72c6-4673-9c12-f3356cd70ac0)  |  ![image](https://github.com/texttechnologylab/Viki-LibraRy/assets/49918134/57680af4-7a93-4f46-a491-e9c6771994d5)
Excerpt from Viki LibraRy: Reading the Wikipedia article of “Rome” as a Book together with another user. In the background stairs to other Rooms as well as other Rooms on the same floor are shown. | Room "Geography" under construction from an outside view, where the players can then traverse in and experience the Category "Geography" of Wikipedia in VR.

# Showcase
[![Viki LibraRy](https://img.youtube.com/vi/jEzctkv2MXo/0.jpg)](https://www.youtube.com/watch?v=jEzctkv2MXo)

# Installation

1. Clone this repository
2. Get the latest release from GitHub. This contains the Unity project
3. In the repository, direct to the Java/viki_library_apiserver folder. Open the maven project with an IDE of your liking, install the Maven dependencies with e.g. `mvn install`.
4. There are config files missing in the Java project since we cannot push credentials into the GitHub repo. You have to manually add them to fit your enviroment. To do so:
- Create a new folder under Java/viki_library_apiserver/src called 'resources'. The src folder should now contain 'resources' and 'java'. In the resources folder create the following files:
- `spark-swagger.conf`: Configures swagger. Refer to [Default file values](#default-file-values) for default configurations.
- `wikiApiConfig.properties`: Configures the API. If you didn't add a new API source and just use the default Wikipedia API, refer to [Default file values](#default-file-values) for the default content.
- `vikiLibraRyDbConfig.properties`: Configures the MongoDB used for caching fetched API calls. You need to provide your own MongoDB credentials and then fill the content in the scheme given under [Default file values](#default-file-values)
6. Now you can start the Java server. Default port should be 8080
7. Open the Unity project in Unity downloaded from the Release tab in GitHub *(Make sure you have version 2021.3.25f1 installed or a version that is compatible with it)*. Viki LibraRy works on Desktop as well, but was designed for VR so plug in your VR-Device as well.
8. Open the `APISandbox` Scene.
9. There should be a gameobject `Enviroment\ScriptHolder`. In that gameobject, a script is attached with a property called "API Parameters". Make sure that the API Parameters 'Element 1' is correctly set. It should contain the base url for the afore started Java web server. If you started the Java server locally and didn't change the port, this should be correct by default.
10. Start the project in Unity and you are set to go!

# Default file values

## Swagger config:
```
spark-swagger {

  # UI related configs
  theme = "MATERIAL"
  deepLinking = false
  displayOperationId = false
  defaultModelsExpandDepth = 1
  defaultModelExpandDepth = 1
  defaultModelRendering = "model"
  displayRequestDuration = false
  docExpansion = "LIST"
  filter = true
  operationsSorter = "alpha"
  showExtensions = false
  showCommonExtensions = false
  tagsSorter = "alpha"


  # API related configs
  host = "localhost:8080"
  basePath = "/swagger"
  docPath = "/doc"
  info {
    description = "API designed to serve all network operations"
    version = "1.0.0"
    title = "VikiLibraRy API"
    termsOfService = ""
    schemes = ["HTTP", "HTTPS", "WS", "WSS"]
    project {
      groupId = "com.beerboy.thor"
      artifactId = "thor-hammer"
      version = "1.0.0"
    }
    contact {
      name = "Example Team"
      email = "example@team.com"
      url = "example.team.com"
    }
    license {
      name = "Apache 2.0"
      url = "http://www.apache.org/licenses/LICENSE-2.0.html"
    }
    externalDoc {
      description = "Example Doc"
      url="com.example.doc"
    }
  }
}
```

## Wiki API config:
```
default_query_parameters = action=query&generator=categorymembers&prop=categories|categoryinfo|revisions&cllimit=max&gcmlimit=max&format=json&gcmtitle=Category:{CATEGORY}
default_page_text_parse_parameters = action=parse&pageid={PAGEID}&formatversion=2&format=json&prop=wikitext|revid
base_url = https://en.wikipedia.org/w/api.php?
```

## MongoDB config:
```
remote_host = MY_HOST
remote_database = MY_DATABASE
remote_user = MY_USER
remote_password = MY_PW
remote_port = MY_PORT
```

# Cite
If you want to use the project please quote this as follows:

S. Babbili, K. Bönisch, Y. Heinrich, P. Stephan, G. Abrami, and A. Mehler, “Viki LibraRy: A virtual reality library for collaborative browsing and navigation through hypertext,” in Proceedings of 34th ACM Hypertext Conference (HT 23), 2023. accpeted

## BibTeX
```
@inproceedings{Babbili:et:al:2023,
    author    = {Babbili, Shaduan and Bönisch, Kevin and Heinrich, Yannick and Stephan, Philipp and Abrami, Giuseppe and Mehler, Alexander},
    title     = {Viki LibraRy: A virtual reality library for collaborative browsing and navigation through hypertext},
    booktitle = {Proceedings of 34th ACM Hypertext Conference (HT 23)},
    year      = {2023},
    location  = {Rome},
    note      = {accpeted}
}
```
