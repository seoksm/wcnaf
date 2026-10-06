package com.winitech.common.library.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * com.winitech.common.library.core
 * └ CircularReferenceDetector.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-17 17:09
 **/
public class CircularReferenceDetector {
		// 순환 참조 여부를 확인하는 메서드
		public static <T> boolean hasCircularReference(Map<T, Set<T>> graph) {
			return findFirstCyclicNode(graph) != null;
		}

		public static <T> T findFirstCyclicNode(Map<T, Set<T>> graph) {
			Set<T> visited = new HashSet<>(); // 방문한 노드
			Set<T> recursionStack = new HashSet<>(); // 현재 재귀 호출 스택에 있는 노드

			for (T node : graph.keySet()) {
				T firstCycleNode = deepestFirstSearch(node, graph, visited, recursionStack);
				if (firstCycleNode != null) {
					// 순환 발견
					return firstCycleNode;
				}
			}
			// 순환 없음
			return null;
		}

		// DFS 기반 순환 탐색
		private static <T> T deepestFirstSearch(T node, Map<T, Set<T>> graph, Set<T> visited, Set<T> recursionStack) {
			if (recursionStack.contains(node)) {
				return node; // 순환 발견
			}
			if (visited.contains(node)) {
				return null; // 이미 확인한 노드
			}

			// 방문 처리
			visited.add(node);
			recursionStack.add(node);

			// 인접 노드 탐색
			if (graph.get(node) != null) {
				for (T neighbor : graph.get(node)) {
					T firstCycleNode = deepestFirstSearch(neighbor, graph, visited, recursionStack);

					if (firstCycleNode != null) {
						return firstCycleNode;
					}
				}
			}

			// 탐색 종료 후 스택에서 제거
			recursionStack.remove(node);
			return null;
		}
	}