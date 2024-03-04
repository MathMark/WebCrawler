Project represents a web spider that crawls any website and collects important notes about it such as broken links.

<h2>How to start the app</h2>

<ol>
  <li>Build a docker image:</li>
  <i>docker build . -t webcrawler</i>
  <li>Open docker-compose.yml and run the command:</li>
  <i>docker-compose up</i>
  <li>Open localhost:8294/swagger-ui/index.html in your browser to see API documentation.</li>
</ol>

You can also start the app without using containers. To do this:

<ol>
  <li>Run mongodb on default port 27017</li>
  <li>Open application.yml file and change mongodb url to: "mongodb://localhost:27017/websites"</li>
  <li>Build the app:</li>
  <i>mvn clean install</i>
  <li>Run the app:</li>
  <i>java -jar webcrawler-0.0.1-SNAPSHOT.jar</i>
</ol>
