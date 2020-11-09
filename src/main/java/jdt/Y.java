package jdt;

import mrmathami.cia.java.JavaCiaException;
import mrmathami.cia.java.project.JavaProjectSnapshot;
import mrmathami.cia.java.project.JavaProjects;
import mrmathami.cia.java.tree.dependency.JavaDependency;
import mrmathami.cia.java.tree.dependency.JavaDependencyWeightTable;
import mrmathami.utils.Pair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Y {
	public static final JavaDependencyWeightTable DEPENDENCY_WEIGHT_TABLE = JavaDependencyWeightTable.of(Map.of(
			JavaDependency.USE, 1.0,
			JavaDependency.MEMBER, 1.0,
			JavaDependency.INHERITANCE, 4.0,
			JavaDependency.INVOCATION, 4.0,
			JavaDependency.OVERRIDE, 1.0
	));

	public static void main(String[] args) throws JavaCiaException, IOException {
//		System.in.read();

//		CodeFormatter

		//final Path corePath = Path.of("D:\\Research\\SourceCodeComparator\\javacia\\core\\src\\main\\java");
		final Path corePath = Path.of("D:\\project\\JavaCIA\\core\\src\\main\\java");
		//final Path corePath = Path.of("D:\\project\\calculator\\target\\Simple-Java-Calculator-1.0-SNAPSHOT-jar-with-dependencies.jar");
		final List<Path> coreFiles = getFileList(new ArrayList<>(), corePath);
		//final Path jdtPath = Path.of("D:\\Research\\SourceCodeComparator\\javacia\\jdt\\src\\main\\java");
		final Path jdtPath = Path.of("D:\\project\\JavaCIA\\jdt\\src\\main\\java");
		final List<Path> jdtFiles = getFileList(new ArrayList<>(), jdtPath);
		final Map<String, Pair<Path, List<Path>>> javaSources = Map.of(
				"core", Pair.immutableOf(corePath, coreFiles),
				"jdt", Pair.immutableOf(jdtPath, jdtFiles)
		);

		final List<Path> classPaths = List.of(
				Path.of("C:\\Users\\S14\\.m2\\repository\\mrmathami\\external\\org.eclipse.jdt.core\\3.23.0-fat\\org.eclipse.jdt.core-3.23.0-fat.jar"),
				Path.of("C:\\Users\\S14\\.m2\\repository\\mrmathami\\mrmathami.utils\\1.0.1\\mrmathami.utils-1.0.1.jar")
//				Path.of("C:\\Users\\S14\\.m2\\repository\\junit\\junit\\4.12\\junit-4.12.jar"),
//				Path.of("C:\\Users\\S14\\.m2\\repository\\org\\hamcrest\\hamcrest-core\\1.3\\hamcrest-core-1.3.jar"),
//				Path.of("C:\\Users\\S14\\.m2\\repository\\log4j\\log4j\\1.2.17\\log4j-1.2.17.jar")
		);

		long timeStart = System.nanoTime();
		final JavaProjectSnapshot projectSnapshot = JavaProjects.createProjectSnapshot("before",
				javaSources, classPaths, DEPENDENCY_WEIGHT_TABLE);
		long timeParseA = System.nanoTime();

		final String jsonA = projectSnapshot.getRootNode().toJson();

		Files.write(corePath.resolve("output.txt"), jsonA.getBytes(StandardCharsets.UTF_8));

		System.out.printf("Parse A time: %s\n", (timeParseA - timeStart) / 1000000.0);
	}

	private static List<Path> getFileList(List<Path> fileList, Path dir) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path path : stream) {
				if (path.toFile().isDirectory()) {
					getFileList(fileList, path);
				} else {
					fileList.add(path);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileList;
	}
}
