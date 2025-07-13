path="info/kgeorgiy/ja/zhunusov/implementor"
javac -cp "../../java-advanced-2025/artifacts/*" ../java-solutions/${path}/Implementor.java
jar -cfm implementor.jar MANIFEST.MF ../java-solutions/${path}/Implementor.class