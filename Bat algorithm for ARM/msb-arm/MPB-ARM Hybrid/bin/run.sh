#!/bin/bash  
javac com/kaliheragmi/batarm/*.java
java -Xmx2g com.kaliheragmi.batarm.Preprocessing
java -Xmx2g com.kaliheragmi.batarm.main_bat
