package com.google.idea.sdkcompat.scala;

import org.jetbrains.plugins.scala.project.ScalaLibraryProperties;
import scala.Option;
import scala.collection.immutable.Seq;
import scala.collection.immutable.Seq$;
import scala.jdk.javaapi.CollectionConverters;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ScalaCompat {
    public static ScalaLibraryProperties scalaLibraryProperties(
            Option<String> libraryVersion, List<Path> compilerClasspath) {
        List<File> files = new ArrayList<>();
        for (Path path : compilerClasspath) {
            files.add(path.toFile());
        }
        Seq<File> classpath = CollectionConverters.asScala(files).toSeq();
        return ScalaLibraryProperties.apply(libraryVersion, classpath, Seq$.MODULE$.<File>empty());
    }
}
