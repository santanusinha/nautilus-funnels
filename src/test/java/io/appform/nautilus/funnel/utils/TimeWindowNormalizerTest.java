package io.appform.nautilus.funnel.utils;

import io.appform.nautilus.funnel.model.filter.TimeWindow;
import io.dropwizard.util.Duration;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by guruprasad.sridharan on 08/02/16.
 */
public class TimeWindowNormalizerTest {

    @Test
    public void testNormalize() throws Exception {
        final DateTime start = DateTime.parse("2010-12-01T05:30:00Z");
        final DateTime end = DateTime.now();
        TimeWindow tw1 = TimeWindowNormalizer.normalize(TimeWindow.builder().start(start).end(end).build());

        Assert.assertTrue(start.equals(tw1.getStart()) && end.equals(tw1.getEnd()));

        TimeWindow tw2 = TimeWindowNormalizer.normalize(TimeWindow.builder().start(start).duration(Duration.seconds(5)).build());

        Assert.assertTrue(start.equals(tw2.getStart()) && start.plusSeconds(5).equals(tw2.getEnd()));

        TimeWindow tw3 = TimeWindowNormalizer.normalize(TimeWindow.builder().start(start).build());

        Assert.assertTrue(start.equals(tw3.getStart()));

        TimeWindow tw4 = TimeWindowNormalizer.normalize(TimeWindow.builder().start(start).build(), end);

        Assert.assertTrue(start.equals(tw4.getStart()) && end.equals(tw4.getEnd()));
    }

}
