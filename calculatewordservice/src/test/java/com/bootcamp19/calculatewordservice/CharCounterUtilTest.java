package com.bootcamp19.calculatewordservice;

import com.bootcamp19.calculatewordservice.util.CharCounterUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Unit test murni: tidak butuh Spring, database, atau Docker. */
class CharCounterUtilTest {

    @Test
    void hitungFrekuensi_gajahMati() {
        Map<String, Integer> hasil = CharCounterUtil.hitungFrekuensi("Gajah Mati");

        assertEquals(3, hasil.get("a"));
        assertEquals(1, hasil.get("g"));
        assertEquals(9, CharCounterUtil.totalHuruf(hasil));
        assertEquals("a", CharCounterUtil.hurufTerbanyak(hasil));
    }

    @Test
    void hitungFrekuensi_abaikanAngkaSimbolDanHurufBeraksen() {
        Map<String, Integer> hasil = CharCounterUtil.hitungFrekuensi("café 123 !!");

        assertEquals(3, CharCounterUtil.totalHuruf(hasil)); // c, a, f -> é diabaikan
    }
}
