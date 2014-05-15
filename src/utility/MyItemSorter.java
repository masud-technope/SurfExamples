package utility;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import visitor.CodeObject;

public class MyItemSorter {

	public static HashMap<CodeObject, Integer> sortCodeObjectMap(
			HashMap<CodeObject, Integer> codeObjectMap) {
		// code for sorting the hash map
		List<Map.Entry<CodeObject, Integer>> list = new LinkedList<>(
				codeObjectMap.entrySet());
		Collections.sort(list, new CustomComparator_cobjInt());
		// adding sorted item in the list
		Map<CodeObject, Integer> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<CodeObject, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		// returning the sorted item
		return (HashMap<CodeObject, Integer>) sortedMap;
	}

	public static class CustomComparator_cobjInt implements
			Comparator<Map.Entry<CodeObject, Integer>> {
		@Override
		public int compare(Map.Entry<CodeObject, Integer> e1,
				Map.Entry<CodeObject, Integer> e2) {
			Integer value1 = e1.getValue();
			Integer value2 = e2.getValue();
			return value2.compareTo(value1);
		}
	}

}
