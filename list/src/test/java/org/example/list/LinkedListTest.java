/*
 * Copyright 2025 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.list;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {
    @Test
    void testConstructor() {
        LinkedList list = new LinkedList();
        assertEquals(0, list.size());
    }

    @Test
    void testAdd() {
        LinkedList list = new LinkedList();

        list.add("one");
        assertEquals(1, list.size());
        assertEquals("one", list.get(0));

        list.add("two");
        assertEquals(2, list.size());
        assertEquals("two", list.get(1));
    }

    @Test
    void testRemove() {
        LinkedList list = new LinkedList();

        list.add("one");
        list.add("two");
        assertTrue(list.remove("one"));

        assertEquals(1, list.size());
        assertEquals("two", list.get(0));

        assertTrue(list.remove("two"));
        assertEquals(0, list.size());
    }

    @Test
    public void testRemoveMissing() {
        LinkedList list = new LinkedList();

        list.add("one");
        list.add("two");
        assertFalse(list.remove("three"));
        assertEquals(2, list.size());
    }
}
