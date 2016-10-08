package com.novoda.test

import com.novoda.staticanalysis.Penalty
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

abstract class TestProject {
    private final BuildScriptBuilder buildScriptBuilder
    private final File projectDir
    private final GradleRunner gradleRunner

    TestProject(BuildScriptBuilder buildScriptBuilder) {
        this.buildScriptBuilder = buildScriptBuilder
        this.projectDir = createProjectDir("${System.currentTimeMillis()}")
        this.gradleRunner = GradleRunner.create()
                .withProjectDir(projectDir)
                .withDebug(true)
                .withPluginClasspath()
                .forwardStdOutput(new OutputStreamWriter(System.out))
                .forwardStdError(new OutputStreamWriter(System.out))
    }

    private static File createProjectDir(String path) {
        File dir = new File(Fixtures.BUILD_DIR, "test-projects/$path")
        dir.deleteDir()
        dir.mkdirs()
        return dir
    }

    List<String> defaultArguments() {
        Collections.singletonList('--stacktrace')
    }

    void copyFile(File source, String path) {
        File file = new File(gradleRunner.projectDir, path)
        file.parentFile.mkdirs()
        file.text = source.text
    }

    public TestProject withSrcDirs(File... srcDirs) {
        buildScriptBuilder.withSrcDirs(srcDirs)
        return this
    }

    public TestProject withPenalty(Penalty penalty) {
        buildScriptBuilder.withPenalty(penalty)
        return this
    }

    public Result build(String... arguments) {
        copyFile(Fixtures.CHECKSTYLE_CONFIG, 'config/checkstyle/checkstyle.xml')
        new File(projectDir, 'build.gradle').text = buildScriptBuilder.build()
        new Result(withArguments(arguments).build())
    }

    public Result buildAndFail(String... arguments) {
        copyFile(Fixtures.CHECKSTYLE_CONFIG, 'config/checkstyle/checkstyle.xml')
        new File(projectDir, 'build.gradle').text = buildScriptBuilder.build()
        new Result(withArguments(arguments).buildAndFail())
    }

    private GradleRunner withArguments(String... arguments) {
        List<String> defaultArgs = defaultArguments()
        List<String> args = new ArrayList<>(arguments.size() + defaultArgs.size())
        args.addAll(defaultArgs)
        args.addAll(arguments)
        gradleRunner.withArguments(args)
    }

    public void deleteDir() {
        projectDir.deleteDir()
    }

    public static class Result {
        private final BuildResult buildResult

        Result(BuildResult buildResult) {
            this.buildResult = buildResult
        }

        BuildResult getBuildResult() {
            buildResult
        }
    }
}
