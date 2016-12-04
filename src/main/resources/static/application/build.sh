#!/usr/bin/env bash
ng build -prod
index=$(sed  -e 's/href="f/href="application\/dist\/f/' \
     -e 's/href="s/href="application\/dist\/s/' \
     -e 's/src="/src="application\/dist\//g' ./dist/index.html)

echo "$index" > ./dist/index.html
