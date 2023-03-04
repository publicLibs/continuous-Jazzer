cd tmp/ ||exit 1
	wget -c https://github.com/CodeIntelligenceTesting/jazzer/releases/latest/download/jazzer-{linux,macos,windows}.tar.gz ||exit 2
		rm linux/ windows/ macos/ -rfv 
		rm jazzer-{linux,macos}{,.jar} jazzer-windows.{exe,jar} -v

		tar -xavf jazzer-linux.tar.gz||exit 3
			mv jazzer_standalone.jar jazzer-linux.jar
			mv jazzer jazzer-linux
		tar -xavf jazzer-macos.tar.gz||exit 4
			mv jazzer_standalone.jar jazzer-macos.jar
			mv jazzer jazzer-macos
		tar -xavf jazzer-windows.tar.gz||exit 5
			mv jazzer_standalone.jar jazzer-windows.jar
			mv jazzer jazzer-windows.exe
cd ../

rm src/main/resources/jazzer-* -rfv
	cp tmp/jazzer-{windows,linux,macos}.jar src/main/resources/ -v || exit 6
	cp tmp/jazzer-{linux,macos}  src/main/resources/ -v|| exit 6
	cp tmp/jazzer-windows.exe src/main/resources/ -v|| exit 6
		rm tmp/jazzer-linux -v
		rm tmp/jazzer-linux.jar -v
		rm tmp/jazzer-linux.tar.gz -v
		rm tmp/jazzer-macos -v
		rm tmp/jazzer-macos.jar -v
		rm tmp/jazzer-macos.tar.gz -v
		rm tmp/jazzer-windows.exe -v
		rm tmp/jazzer-windows.jar -v
		rm tmp/jazzer-windows.tar.gz -v
