# Valorant-ELO-Java

<p align="center">
  <img src="https://i.imgur.com/ifOXjnn.png" width="700">
</p>


## This program is not affiliated with Riot Games in any way
If this needs to be taken down for whatever reason, please contact us. 

## About
First we want to give a huge shoutout to [RumbleMike and his ValorantStreamOverlay project](https://github.com/RumbleMike/ValorantStreamOverlay), none of this would have possible without his work. Make sure to check it out if you haven't already. 

This program lets you visualize your ELO as you play competitive games. It keeps a history of your played games even after they are overwritten by your newer games. It supports up to 3 accounts so you can easily switch them. You may be wondering why use this program if Riot already implmented the Ranked Rating numbers into the game. This program caches all your games, meaning that even after the matches leave your career page they'll remain on the graph for you to see. It's also nice (or depressing) to visually see your ELO increase/decrease on a basic line graph.

Your username and password are used to make a call to the Riot API exclusively. We do not do anything with your account information. The code is open-source, if you want to compile the program into a jar yourself, you can. You do not have to save your account info, however, if you do then the program will save your username onto a textfile and the password will be encrypted in Base64.

## External Packages
[Unirest](https://kong.github.io/unirest-java/)

[GSON](https://github.com/google/gson)

[Dropbox](https://github.com/dropbox/dropbox-sdk-java)

[Log4j 2](https://logging.apache.org/log4j/2.x/)

## Installation/Demo
Overall the program is very simple and straight forward. You should be able to extract the zip and just run the program. If for some reason you cannot run the program, you probably need to have Java installed first which you can find [here](https://www.java.com/en/download/).

Here's a gif on how to use the program

<p align="center">
  <img src="/demo.gif">
</p>


If Windows Defender is on, Windows will prevent you from opening the program due to the exe not being signed. To get past this, simply click more info and then run. This message shouldn't pop up again. Once again, you can check all the source code and compile it yourself. The code is completely clean.

<p align="center">
  <img src="https://i.imgur.com/tKt72qu.png" width="400">
</p>


## Contact
<p>
  <a href="https://twitter.com/FarFar0204"><img src="https://img.shields.io/badge/Twitter-@FarFar0204-1da1f2.svg?logo=twitter?style=for-the-badge&logo=appveyor"></a>
  <a><img src ="https://img.shields.io/badge/Discord-fofsfofs%230204-blueviolet?logo=discord"></a>
    <a><img src ="https://img.shields.io/badge/Discord-Torkoal64%233969-blueviolet?logo=discord"></a>
</p>
