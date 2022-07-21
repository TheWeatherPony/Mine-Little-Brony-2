# Mine Little Brony 2
A complete rewrite of the old Mine Little Brony mod for Minecraft, still based on the Mine Little Pony mod (this time as a dependency, instead of an outdated version included).
This rewrite has the pony entities overengineered to the extreme, and are even written in half Java (contracts and interfaces) and half Scala (details and implementations); Scala gets compiled into Java Bytecode.
The rest of the mod is in Java.

###### Code Guidelines
These code style guidelines are for pulls/merges/etc. -- I don't care how you write your own downstream projects
- Use tabs for indenting at the start of lines
- Classes use CammelCase while methods, fields, and local variables use cammelCase
- Interfaces start with the letter I; "bonus points" for using that I as a word (ex: IAmAPony)
  - this rule does not count for Scala traits that cannot be directly converted to a Java interface
- The only documentation that should be needed to understand the code is to label when things aren't coded completely, or to comment code history to make future updates easier. If a method/field/class needs explaining, then it needs better naming
- No repeatedly checking instance equality in a mass of conditional statements
  - leave that for Pam's HC
- Although this is a mod about ponies, don't ponify the code -- no naming World variables "equestria", etc.

###### TODO (what new areas need to be written, and are fair game for anyone to make pull/merge requests on)
- Pony spawning (natural, not from a spawn-egg)
- FlutterShy trees
- Cloud blocks, decorative cloud blocks, cloud structures
- Everfree Forest biome
  - Poison Joke
  - Timberwolves
- Fruit trees, recipes involving the fruits
- Nearly anything else MLP:FiM themed (will require mentioning {the/a season&episode numbers / the MLP:FiM movie name / the fanfiction(s?) / etc.} being used as a source for the idea

###### Ponies (which language you need to use for adding your own downstream content)
- new ponies (skins&voices, pony-specific logic) -- Java
- AI -- depends: if you need to add data to the pony, then Scala; else Java
- interactions (right click with item) -- see AI. can be applied generally as AI or pony-specific to the PonyLogic