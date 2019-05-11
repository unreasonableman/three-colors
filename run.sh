#! /bin/sh
javac *.java || exit 1
java ThreeColors -input input.txt -threadcount 8

