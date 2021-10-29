/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.showcase.services.song.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.skywalking.showcase.services.song.entity.Song;
import org.apache.skywalking.showcase.services.song.repo.SongsRepo;
import org.apache.skywalking.showcase.services.song.vo.TrendingList;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/songs")
public class SongController {
    private final SongsRepo songsRepo;
    private final RestTemplate restTemplate;

    @GetMapping
    public List<Song> songs() {
        return songsRepo.findAll();
    }

    @GetMapping("/top")
    public TrendingList top() {
        final List<Song> top = songsRepo.findByLikedGreaterThan(1000);
        final ResponseEntity<List<Song>> res = restTemplate.exchange(
            "/rcmd", HttpMethod.GET, null, new ParameterizedTypeReference<List<Song>>() {
            });
        if (res.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to get recommendations");
        }
        return new TrendingList(top, res.getBody());
    }
}