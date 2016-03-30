cd ..
java -Xms4096m -Xmx8192m -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl -jar dist/Etosha_WikiExplorer_0.3.2.jar wikipedia.corpus.extractor.WikipediaCorpusLoaderTool 
cd bin
