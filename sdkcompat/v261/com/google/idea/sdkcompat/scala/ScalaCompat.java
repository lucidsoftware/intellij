package com.google.idea.sdkcompat.scala;

import org.jetbrains.plugins.scala.project.ReplClasspath;
import org.jetbrains.plugins.scala.project.ScalaLibraryProperties;
import scala.Option;
import scala.collection.immutable.Seq;
import scala.collection.immutable.Seq$;
import scala.jdk.javaapi.CollectionConverters;

import java.nio.file.Path;
import java.util.List;

public class ScalaCompat {
    public static ScalaLibraryProperties scalaLibraryProperties(
            Option<String> libraryVersion, List<Path> compilerClasspath) {
        Seq<Path> classpath = CollectionConverters.asScala(compilerClasspath).toSeq();
        return ScalaLibraryProperties.apply(
                libraryVersion,
                classpath,
                Seq$.MODULE$.<Path>empty(),
                Option.empty(),
                ReplClasspath.fromPaths(Seq$.MODULE$.<Path>empty()));
    }
}
