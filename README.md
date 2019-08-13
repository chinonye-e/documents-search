# documents-search

To run this program:
1. Clone and checkout into a Java IDE such as eclipse or IntelliJ
2. Build project using maven.
3. Right-click on project and select Run As > Java Application
4. Follow the promptings of the program.
  
Note that this program also executes performance search. You'll be given the choice to run this performance search, and you can provide the number of executions to run with the maximum being two million. This generates up to two million random strings (all lower case and alphabets only) and executes the simple string matching, regex matching, as well as index string matching using lucene. Beware that the index string matching is the slowest and could take a while to execute if the performance search input is high enough.
