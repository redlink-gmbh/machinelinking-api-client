/*
 *  Copyright 2013 MachineLinking S.r.l.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.machinelinking.api.client;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for {@link APIClient} class.
 *
 * @author Michele Mostarda (michele@machinelinking.com)
 */
public class APIClientTest {

    private final String LANG_DETECTION_EN_SAMPLE = "This is an example of English language.";
    private final String LANG_DETECTION_IT_SAMPLE = "Questo è un esempio di lingua italiana.";

    private final String ANNOTATE_TEXT_SAMPLE = "Superman is a fictional character, a comic book superhero " +
            "who appears in comic books published by DC Comics. He is widely considered to be an American cultural " +
            "icon. Created by American writer Jerry Siegel and Canadian-born American artist Joe Shuster in 1932 " +
            "while both were living in Cleveland, Ohio, and sold to Detective Comics, Inc. (later DC Comics) in 1938, " +
            "the character first appeared in Action Comics #1 (June 1938) and subsequently appeared in various radio " +
            "serials, television programs, films, newspaper strips, and video games. With the success of his " +
            "adventures, Superman helped to create the superhero genre and establish its primacy within the American " +
            "comic book. The character's appearance is distinctive and iconic: a blue, red and yellow costume, " +
            "complete with cape, with a stylized \"S\" shield on his chest. This shield is typically used across media" +
            " to symbolize the character.";

    private final String COMPARE_TEXT1_SAMPLE =
            "Batman soprannominato Il giustiziere o Il Cavaliere Oscuro, " +
            "è un personaggio dei fumetti creato da Bob Kane e Bill Finger.";

    private final String COMPARE_TEXT2_SAMPLE = "Superman is a fictional character, a comic book superhero " +
            "who appears in comic books published by DC Comics.";

    private APIClient client;

    @Before
    public void setUp() {
        client = new APIClient(TestConstants.APP_ID, TestConstants.APP_KEY);
    }

    @After
    public void tearDown() {
       client = null;
    }

    @Test
    public void testLanguageDetectionEN() throws IOException, APIClientException {
        final GuessedLanguageResponse guessedLanguage = client.guessLanguage(LANG_DETECTION_EN_SAMPLE);
        Assert.assertEquals("en", guessedLanguage.getLang());
    }

    @Test
    public void testLanguageDetectionIT() throws IOException, APIClientException {
        final GuessedLanguageResponse guessedLanguage = client.guessLanguage(LANG_DETECTION_IT_SAMPLE);
        Assert.assertEquals("it", guessedLanguage.getLang());
    }

    @Test
    public void testAnnotationExtraction() throws IOException, APIClientException {
        final AnnotationResponse annotation = client.annotate(ANNOTATE_TEXT_SAMPLE);
        Assert.assertEquals("en", annotation.getLang());
        Assert.assertTrue(annotation.getKeywords().length >= 10);
        Assert.assertTrue(annotation.getTopics().length >= 1);

        final Keyword superman = filterBySense(annotation.getKeywords(), "Superman");
        assertConfidence("Rel", superman.getRel());
        Assert.assertEquals("Superman", superman.getSensePage());
        assertConfidence("Sense Probability", superman.getSenseProbability());
        Assert.assertEquals("Superman", superman.getForm());
        Assert.assertEquals(2, superman.getNGrams().length);
        Assert.assertTrue(superman.getAbstract().length() > 100);
        Assert.assertTrue(superman.getClasses().length > 1);
        Assert.assertTrue(superman.getCategories().length > 10);
        Assert.assertTrue(superman.getExternals().length > 1);
        Assert.assertTrue(superman.getCrosses().length > 5);
        Assert.assertTrue(superman.getTopics().length > 2);
        Assert.assertTrue(superman.getAlts().length > 10);
        Assert.assertTrue(superman.getImages().length > 5);
    }

    @Test
    public void testTextComparison() throws IOException, APIClientException {
        final CompareResponse compare = client.compare(
                ComparisonMethod.combo, COMPARE_TEXT1_SAMPLE, COMPARE_TEXT2_SAMPLE
        );
        Assert.assertTrue(compare.getSimilarity() > 0.4d);
    }

    @Test
    public void testTextSummarization() throws IOException, APIClientException {
        final SummaryResponse summary = client.summarize(CompressionMethod.sum, 0.3f, ANNOTATE_TEXT_SAMPLE);
        Assert.assertTrue(summary.getSummaries().length > 1);
        for(Summary phrase : summary.getSummaries()) {
            Assert.assertTrue(phrase.getStart() < phrase.getEnd());
            Assert.assertTrue(phrase.getSentence().length() > 10);
            Assert.assertTrue(phrase.getWeight() >= 0.3);
        }
    }

    private void assertConfidence(String description , float value) {
        Assert.assertTrue(
                String.format("Invalid value for [%s]", description),
                value >= 0 && value <= 1
        );
    }

    private Keyword filterBySense(Keyword[] keywords, String sense) {
        for(Keyword keyword : keywords) {
            if(sense.equals(keyword.getSensePage())) return keyword;
        }
        throw new IllegalStateException("Invalid test preconditions.");
    }



}
