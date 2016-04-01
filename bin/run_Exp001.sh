cd ..
echo  Nr of time series  : $1
echo exponent for length : $2
echo                beta : $3
echo                mode : $4
echo              NO LTM : $5

java -Xms4096m -Xmx8192m -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl -cp target/WikiExplorer.NG-1.0.0.jar:target/WikiExplorer.NG-1.0.0-dependencies.jar experiments.CorrelationPropertiesExperiment001 $1 $2 $3 $4 $5
cd bin
