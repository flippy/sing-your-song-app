package dev.flippy.singyoursong;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
        import java.util.HashMap;
        import java.util.LinkedHashMap;
        import java.util.List;
        import java.util.Locale;
import java.util.Map;
import java.util.Set;

        import android.content.Context;
        import android.util.Log;
        import android.widget.ArrayAdapter;
        import android.widget.SectionIndexer;
import android.widget.SimpleAdapter;

public class IndexableListAdapter extends SimpleAdapter implements SectionIndexer {

    HashMap<String, Integer> mapIndex;
    String[] sections;
    List<? extends Map<String, String>> items;

    public IndexableListAdapter(Context context, List<? extends Map<String, String>> data, int resource, String[] from, int[] to, String titleProperty) {
        super(context, data, resource, from, to);

        this.items = data;
        mapIndex = new LinkedHashMap<String, Integer>();

        for (int x = 0; x < items.size(); x++) {
            String title = items.get(x).get(titleProperty);

            // Support empty titles.
            title += "#";

            String ch = title.substring(0, 1);
            ch = ch.toUpperCase(Locale.US);

            if (!mapIndex.containsKey(ch)) {
                mapIndex.put(ch, x);
            }
        }

        Set<String> sectionLetters = mapIndex.keySet();

        // create a list from the set to sort
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
        Collections.sort(sectionList);

        sections = new String[sectionList.size()];

        sectionList.toArray(sections);
    }

    public int getPositionForSection(int section) {
        if (section < 0) {
            return 0;
        }
        if (section >= sections.length) {
            section = sections.length - 1;
        }
        return mapIndex.get(sections[section]);
    }

    public int getSectionForPosition(int position) {
        for (Map.Entry<String, Integer> entry : mapIndex.entrySet()) {
            if (position < entry.getValue()) {
                return (Arrays.asList(sections).indexOf(entry.getKey()) - 1);
            }
        }
        return 0;
    }

    public Object[] getSections() {
        return sections;
    }
}