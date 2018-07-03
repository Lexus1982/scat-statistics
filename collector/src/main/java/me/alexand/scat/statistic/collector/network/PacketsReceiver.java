package me.alexand.scat.statistic.collector.network;

import me.alexand.scat.statistic.collector.model.RawDataPacket;

/**
 * Абстракия для получения IPFIX-сообщений в виде набора байт от экспортера независимо от
 * типа транспортного протокола, используемого для передачи сообщений (tcp, udp, etc).
 *
 * @author asidorov84@gmail.com
 */
public interface PacketsReceiver {
    /**
     * Получить очередной пакет с необработанными данными.<br>
     * Вызов блокирующий, до тех пор, пока не будет получен очередной пакет.
     *
     * @return пакет с необработанными данными
     * @throws InterruptedException
     */
    RawDataPacket getNextPacket() throws InterruptedException;
}