/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.enterprise.inject.Instance;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.guvnor.common.services.project.model.Package;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kie.workbench.common.screens.library.api.index.LibraryFileNameIndexTerm;
import org.kie.workbench.common.screens.library.api.index.LibraryProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.ImpactAnalysisAnalyzerWrapperFactory;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.LowerCaseOnlyAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQueries;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectRootPathIndexTerm;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.ext.metadata.io.IndexersFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

import static org.mockito.Mockito.*;

public abstract class BaseLibraryIndexingTest {

    public static final String TEST_PROJECT_ROOT = "/a/mock/project/root";
    public static final String TEST_PROJECT_NAME = "mock-project";
    public static final String TEST_PACKAGE_NAME = "org.kie.workbench.mock.package";
    protected static final Logger logger = LoggerFactory.getLogger(BaseLibraryIndexingTest.class);
    protected static final List<File> tempFiles = new ArrayList<>();
    private static LuceneConfig config;
    protected int seed = new Random(10L).nextInt();
    protected boolean created = false;
    protected Path basePath;
    protected RefactoringQueryServiceImpl service;
    protected IOService ioService = null;

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for (final File tempFile : tempFiles) {
            FileUtils.deleteQuietly(tempFile);
        }
        if (config != null) {
            config.dispose();
            config = null;
        }
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws IOException {
        if (!created) {
            final String repositoryName = getRepositoryName();
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty("org.uberfire.nio.git.dir",
                               path);
            System.setProperty("org.uberfire.nio.git.daemon.enabled",
                               "false");
            System.setProperty("org.uberfire.nio.git.ssh.enabled",
                               "false");
            System.setProperty("org.uberfire.sys.repo.monitor.disabled",
                               "true");
            System.out.println(".niogit: " + path);

            final URI newRepo = URI.create("git://" + repositoryName);

            try {
                IOService ioService = ioService();
                ioService.newFileSystem(newRepo,
                                        new HashMap<>());

                // Don't ask, but we need to write a single file first in order for indexing to work
                basePath = getDirectoryPath().resolveSibling("someNewOtherPath");
                ioService().write(basePath.resolve("dummy"),
                                  "<none>");
            } catch (final Exception e) {
                e.printStackTrace();
                logger.warn("Failed to initialize IOService instance: " + e.getClass().getSimpleName() + ": " + e.getMessage(),
                            e);
            } finally {
                created = true;
            }

            final Instance<NamedQuery> namedQueriesProducer = mock(Instance.class);
            when(namedQueriesProducer.iterator()).thenReturn(getQueries().iterator());

            service = new RefactoringQueryServiceImpl(config,
                                                      new NamedQueries(namedQueriesProducer));
            service.init();
        }
    }

    @After
    public void dispose() {
        ioService().dispose();
        ioService = null;
        created = false;
    }

    protected Set<NamedQuery> getQueries() {
        // override me if using the RefactoringQueryServiceImpl!
        return Collections.emptySet();
    }

    protected abstract String getRepositoryName();

    private static File createTempDirectory() throws IOException {
        final File temp = File.createTempFile("temp",
                                              Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }
        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }
        tempFiles.add(temp);
        return temp;
    }

    private Path getDirectoryPath() {
        final String repositoryName = getRepositoryName();
        final Path dir = ioService().get(URI.create("git://" + repositoryName + "/_someDir" + seed));
        ioService().deleteIfExists(dir);
        return dir;
    }


    protected void addTestFile(final String projectName,
                               final String pathToFile) throws IOException {
        final Path path = basePath.resolve(projectName + "/" + pathToFile);
        final String text = loadText(pathToFile);
        ioService().write(path,
                          text);
    }

    protected String loadText(final String fileName) throws IOException {
        InputStream fileInputStream = this.getClass().getResourceAsStream(fileName);
        if (fileInputStream == null) {
            File file = new File(fileName);
            if (file.exists()) {
                fileInputStream = new FileInputStream(file);
            }
        }
        final BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    @SuppressWarnings("unchecked")
    protected IOService ioService() {
        if (ioService == null) {
            final Map<String, Analyzer> analyzers = getAnalyzers();
            LuceneConfigBuilder configBuilder = new LuceneConfigBuilder()
                    .withInMemoryMetaModelStore()
                    .usingAnalyzers(analyzers)
                    .usingAnalyzerWrapperFactory(ImpactAnalysisAnalyzerWrapperFactory.getInstance())
                    .useInMemoryDirectory()
                    // If you want to use Luke to inspect the index,
                    // comment ".useInMemoryDirectory(), and uncomment below..
//                     .useNIODirectory()
                    .useDirectoryBasedIndex();

            if (config == null) {
                config = configBuilder.build();
            }

            ioService = new IOServiceIndexedImpl(config.getIndexEngine());

            final LibraryIndexer indexer = new LibraryIndexer(new LibraryAssetTypeDefinition());
            IndexersFactory.clear();
            IndexersFactory.addIndexer(indexer);

            //Mock CDI injection and setup
            indexer.setIOService(ioService);
            indexer.setProjectService(getProjectService());
        }
        return ioService;
    }

    private Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {{
            put(LibraryFileNameIndexTerm.TERM,
                new FilenameAnalyzer());
            put(LibraryProjectRootPathIndexTerm.TERM,
                new FilenameAnalyzer());
            put(ProjectRootPathIndexTerm.TERM,
                new FilenameAnalyzer());
            put(PackageNameIndexTerm.TERM,
                new LowerCaseOnlyAnalyzer());
            put(LuceneIndex.CUSTOM_FIELD_FILENAME,
                new FilenameAnalyzer());
        }};
    }

    protected KieProjectService getProjectService() {
        final KieProject mockProject = getKieProjectMock(TEST_PROJECT_ROOT,
                                                         TEST_PROJECT_NAME);

        final Package mockPackage = mock(Package.class);
        when(mockPackage.getPackageName()).thenReturn(TEST_PACKAGE_NAME);

        final KieProjectService mockProjectService = mock(KieProjectService.class);
        when(mockProjectService.resolveProject(any(org.uberfire.backend.vfs.Path.class))).thenReturn(mockProject);
        when(mockProjectService.resolvePackage(any(org.uberfire.backend.vfs.Path.class))).thenReturn(mockPackage);

        return mockProjectService;
    }

    protected KieProject getKieProjectMock(final String testProjectRoot,
                                           final String testProjectName) {
        final org.uberfire.backend.vfs.Path mockRoot = mock(org.uberfire.backend.vfs.Path.class);
        when(mockRoot.toURI()).thenReturn(testProjectRoot);

        final KieProject mockProject = mock(KieProject.class);
        when(mockProject.getRootPath()).thenReturn(mockRoot);
        when(mockProject.getProjectName()).thenReturn(testProjectName);
        return mockProject;
    }
}
