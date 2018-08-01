/*
 * Copyright 2018 Alexander Sidorov (asidorov84@gmail.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package me.alexand.scat.statistic.collector.network;


/**
 * Абстракция для получения IPFIX-сообщений в виде набора байт от экспортера.
 * Реализация может быть выполнена разными способами в зависимости от типа транспортного протокола,
 * используемого экспортером для передачи сообщений (tcp, udp и т.д.).
 *
 * @author asidorov84@gmail.com
 */
public interface PacketsReceiver {
    /**
     * Метод для запуска процесса получения пакетов.
     */
    void start();

    /**
     * Метод для получения очередного пакет с данными.
     * Вызов данного метода блокирующий, до тех пор, пока не будет получен очередной пакет.
     *
     * @return массив байт
     * @throws InterruptedException если во время ожидания очередного пакета текущий поток будет прерван
     */
    byte[] getNextPacket() throws InterruptedException;

    /**
     * Метод для корректной остановки процесса получения пакетов.
     *
     * @return true, если остановка прошла успешно, иначе false
     */
    boolean shutdown();

    /**
     * Методя для получения текущего количества пакетов, находящихся в приемном буфере.
     *
     * @return количество пакетов
     */
    int getRemainingPacketsCount();
}