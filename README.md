# ZLevels
An open-source Discord bot, which adds a levelling system to your server.

## Installing

### Requirements
* Latest Version of Java.
* A MySQL database that you can connect to remotely or locally.

### Installing
1. Download the larest jar file from https://github.com/ZP4RKER/ZLevels/releases.
2. Create a directory/folder anywhere on your compuer and put the jar file inside of it.
3. Create a text document in that folder and copy the following code into it.
```batch
java -jar ZLevels.jar
```
4. Save the text document as run.bat for Windows, or run.sh for macOS and Linux. (Make sure not to save it as run.bat.txt, and make sure to save it into the same directory/folder as the jar).

*If not on macOS go straight to step 7.*

5. Open Terminal.
6. Type `chmod a+x `, then drag the file you just created into the Terminal window, then press enter.
7. Now run the file you just created and close the console once it stops, it should have generated a file called config.yml/
8. Fill in the config.yml with all your preferred configurations. example/explanation:
```ymal
basic-settings:
    name: 'Levels Bot' #the name of your bot
    token: 'This-Is-Totally-A-Real-Token' #the token of your bot user account can be found [here](https://discordapp.com/developers/applications/me) if you have not bmade a bot user yet click the New App button, give it a name and avatar then click create app and then the create bot user button
    prefix: '/' #the characters put before a command. set it to whatever you want
    server: '234534392897339392' #Your server id goes here, you can get it by right clicking your server's icon and clicking copy id in discord
    ops:
    - '145064570237485056' #A list of users that are able to use op only commands in ZLevels (Listed with user id) get a user's id my right clicking their name and clicking copy id
    embed-colour: '#fbae1d' #the colour of embeds. (Hex colours)
    game-status: '' #The Playing _____ message of the bot. set it to whatever you want
database:
    host: '149.56.141.103' #the IP of the mysql server that the Database is located on
    port: '3306' #the port of the mysql server. (3306 is the default port and shouldn't really me anything else)
    name: 'BotDB' #the database name of the bot
    user: 'username' #your mysql username
    pass: 'password123' #your mysql password
more-settings:
    error-length: 600 #Length of errors, Not much need to change this
    autorole-enabled: true $If rewards from roles.yml should be given
staff-ratings: # **Staff Ratings Currently Does Not Work**
    enabled: true #If Ratings is enabled or not
    staff-role: 'Staff' #The Staff Role
    channels: [] #Channels that ratings are accepted in, Tempty for all channels.
```
11. Run the startup file again, then once it says its generated a file called roles.yml, close the console.
12. Add in what roles you want people to get at certain levels. example: (Or leave it blank for no roles to be given.)
```yaml
MEMBER: #The name of the reward
    name: Member #The name of the role to give
    level: 5 #At what level to give the reward
VIP:
    name: Vip
    level: 20
```
9. Run the startup file again.
10. ZLevels is now running, enjoy!

### Credits
* Madzahttr - Writing the instructions.
