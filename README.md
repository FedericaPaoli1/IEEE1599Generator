<!-- PROJECT SHIELDS -->
[![MIT License](https://img.shields.io/github/license/FedericaPaoli1/IEEE1599Generator.svg?branch=master)](https://github.com/FedericaPaoli1/IEEE1599Generator/LICENSE)


<!-- PROJECT LOGO -->
<br />
<p align="center">
  
  <h3 align="center">IEEE1599 Generator</h3>
  
</p>

<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li>
      <a href="#usage">Usage</a>
      <ul>
        <li><a href="#usage-examples">Usage examples</a></li>
      </ul>
    </li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

IEEE 1599 random generator: application which, on the basis of a series of user-definable parameters, generates an IEEE 1599 file by randomly the elements present. 

### Built With

* [Java 11.0.12](https://www.oracle.com/java/technologies/downloads/#java11)
* [Apache Maven 3.8.2](https://maven.apache.org/download.cgi)
* [Apache Maven Javadoc Plugin 3.3.1](https://maven.apache.org/plugins/maven-javadoc-plugin/javadoc-mojo.html)
* [Log4j 2](https://logging.apache.org/log4j/2.x/download.html)
* [Picocli 4.6.1](https://picocli.info/)
* [Jansi 2.0.1](https://fusesource.github.io/jansi/blog/releases/release-2.0.1.html)


<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

* Java 11.0.12
* Apache Maven 3.8.2

### Installation

1. Clone the repo:
   ```sh
   git clone https://github.com/FedericaPaoli1/IEEE1599Generator.git
   ```
2. Run the following command:
   ```sh
   mvn clean package 
   ```
### Usage

1. Within the ` target/` folder, run the following command:

   ```sh
   java -cp IEEE1599Generator-1.0-SNAPSHOT-jar-with-dependencies.jar com.ieee1599generator.IEEE1599App 
   ```
   followed by the parameters:
   
    |                                                        |             Description                |  Default    |
    | -----------------------------------------------------  | -------------------------------------- | ----------- |
    |  --creator=<*String*>                                  | creator name of the IEEE1599 file      |             |
    |  --title=<*String*>                                    | musical composition title              |  *Title*    |
    |  --author=<*String*>                                   | musical composition author             |  *Author*   |
    |  --track-length=<*long*>                               | track length in seconds                |             |
    |  --bpm=<*int*>                                         | time expressed in bpm                  |             |
    |  --metre=<*first parameter*:*second parameter*>        | metre expressed as a string            |             |
    |  --instruments-number=<*int*>                          | musical instruments number             |             |
    |                                                        |                                        |             |
    
2. Specify the following parameters as many times as there are musical instruments (instruments-number):

    |                                                       |                        Description                                |  Default    |
    | ------------------------------------------------------| ----------------------------------------------------------------- | ----------- |
    |  --max-notes-number=<*int*>                           | maximum number of played notes                                    |             |
    |  --min-duration=<*numerator*/*denominator*>           | minimum duration of musical figures expressed as integers array   |             |
    |  --max-duration=<*numerator*/*denominator*>           | maximum duration of musical figures expressed as integers array   |             |
    |  --min-height=<*Anglo-Saxon note name*>_<*possible accidental* (sharp, sharp_and_a_half, demisharp, double_sharp, flat, flat_and_a_half, demiflat, double_flat)><*octave*>  | minimum height of musical figures expressed as a string |             |
    |  --max-height=<*Anglo-Saxon note name*>_<*possible accidental* (sharp, sharp_and_a_half, demisharp, double_sharp, flat, flat_and_a_half, demiflat, double_flat)><*octave*>  | maximum height of musical figures expressed as a string |             |
    |  --max-notes-number-chord=<*int*>                     | maximum number of notes in a chord                                |             |
    |  --irregular-groups=<*boolean*>                       | presence or absence of irregular groups                           |             |
    |  --min-delay=<*int*>                                  | minimum delay in VTU after which the next note will sound         |             |
    |  --seed=<*long*>                                      | seed for random object                                            |   *1234*    |
    |                                                       |                                                                   |             |

3. Once execution is complete, the following will be generated:

   * the IEEE1599 file, called `ieee1599.xml`
   * the log file, called `ieee1599.log`

4. To generate the Java documentation, run the following command:
   ```sh
   mvn javadoc:javadoc 
   ```
   and in `/target/site/apidocs/` you will find the generated *javadoc* file, called `index.html`.
   
#### Usage examples

After positioning yourself in the `/target` folder:

1. Generation of IEEE1599 file with a single musical instrument:

  ```sh
   java -cp IEEE1599Generator-1.0-SNAPSHOT-jar-with-dependencies.jar com.ieee1599generator.IEEE1599App --creator=Federica --track-length=120 --bpm=108 --metre=4:4  --instruments-number=1 --max-notes-number=150 --min-duration=1/8 --max-duration=1/1 --min-height=C-1 --max-height=A4 --max-notes-number-chord=3 --irregular-groups=false --min-delay=256 
   ```
   
2. Generation of the IEEE1599 file with the presence of irregular groups:

  ```sh
   java -cp IEEE1599Generator-1.0-SNAPSHOT-jar-with-dependencies.jar com.ieee1599generator.IEEE1599App --creator=Federica --track-length=120 --bpm=108 --metre=4:4 --instruments-number=1 --max-notes-number=150 --min-duration=1/8 --max-duration=1/1 --min-height=C-1 --max-height=A4 --max-notes-number-chord=3  --irregular-groups=true --min-delay=256
   ```
   
3. Generation of IEEE1599 file with the presence of accidental in note pitches:

  ```sh
   java -cp IEEE1599Generator-1.0-SNAPSHOT-jar-with-dependencies.jar com.ieee1599generator.IEEE1599App --creator=Federica --track-length=120 --bpm=108 --metre=4:4 --instruments-number=1 --max-notes-number=150 --min-duration=1/8 --max-duration=1/1 --min-height=C_flat_and_a_half2 --max-height=A_sharp_5 --max-notes-number-chord=3 --irregular-groups=false --min-delay=256
   ```
   
4. Generation of IEEE1599 file with more than one musical instrument:

  ```sh
   java -cp IEEE1599Generator-1.0-SNAPSHOT-jar-with-dependencies.jar com.ieee1599generator.IEEE1599App --creator=Federica --track-length=120 --bpm=108 --metre=4:4 --instruments-number=5 --max-notes-number=150 --min-duration=1/8 --max-duration=1/1 --min-height=C-1 --max-height=B_sharp_9 --max-notes-number-chord=3 --irregular-groups=true --min-delay=256 --max-notes-number=200 --min-duration=1/16 --max-duration=1/2 --min-height=E2 --max-height=A4 --max-notes-number-chord=2 --irregular-groups=true --min-delay=256 --max-notes-number=250 --min-duration=1/32 --max-duration=1/4 --min-height=C-1 --max-height=G8 --max-notes-number-chord=5 --irregular-groups=false --min-delay=2 --max-notes-number=50 --min-duration=1/64 --max-duration=1/1 --min-height=D_flat_and_a_half2 --max-height=A5 --max-notes-number-chord=1 --irregular-groups=true --min-delay=100 --max-notes-number=20 --min-duration=1/2 --max-duration=1/1 --min-height=B-1 --max-height=A9 --max-notes-number-chord=7 --irregular-groups=false --min-delay=256
   ```
   
5. Generation of the IEEE1599 file with a different setting of the values that have the default value:

  ```sh
   java -cp IEEE1599Generator-1.0-SNAPSHOT-jar-with-dependencies.jar com.ieee1599generator.IEEE1599App --creator=Federica --title=DifferentTitle --author=DifferentAuthor --track-length=120 --bpm=108 --metre=4:4  --instruments-number=1 --max-notes-number=150 --min-duration=1/8 --max-duration=1/1 --min-height=C-1 --max-height=A4 --max-notes-number-chord=3 --irregular-groups=false --min-delay=256 --seed=5678
   ```

<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.


<!-- CONTACT -->
## Contact

Federica Paoli' - federicapaoli1@gmail.com

Project Link: [https://github.com/FedericaPaoli1/IEEE1599Generator](https://github.com/FedericaPaoli1/IEEE1599Generator)

