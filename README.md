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
8. Fill in the config.yml with all your preferred configurations.
9. Run the startup file again, then once it says its generated a file called roles.yml, close the console.
10. Add in what roles you want people to get at certain levels example: (Or leave it blank for no roles to be given.)
```yaml
MEMBER:
    name: Member
    level: 5
VIP:
    name: Vip
    level: 20
```
9. Run the startup file again.
10. ZLevels is now running, enjoy!

### Credits
* Madzahttr - Writing the instructions.
