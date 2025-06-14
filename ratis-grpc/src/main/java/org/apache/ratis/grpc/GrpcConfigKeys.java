/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ratis.grpc;

import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.server.GrpcServices;
import org.apache.ratis.server.RaftServerConfigKeys;
import org.apache.ratis.util.SizeInBytes;
import org.apache.ratis.util.TimeDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.apache.ratis.conf.ConfUtils.get;
import static org.apache.ratis.conf.ConfUtils.getBoolean;
import static org.apache.ratis.conf.ConfUtils.getInt;
import static org.apache.ratis.conf.ConfUtils.getSizeInBytes;
import static org.apache.ratis.conf.ConfUtils.getTimeDuration;
import static org.apache.ratis.conf.ConfUtils.printAll;
import static org.apache.ratis.conf.ConfUtils.requireMax;
import static org.apache.ratis.conf.ConfUtils.requireMin;
import static org.apache.ratis.conf.ConfUtils.set;
import static org.apache.ratis.conf.ConfUtils.setBoolean;
import static org.apache.ratis.conf.ConfUtils.setInt;
import static org.apache.ratis.conf.ConfUtils.setSizeInBytes;
import static org.apache.ratis.conf.ConfUtils.setTimeDuration;

public interface GrpcConfigKeys {
  Logger LOG = LoggerFactory.getLogger(GrpcConfigKeys.class);
  static Consumer<String> getDefaultLog() {
    return LOG::info;
  }

  String PREFIX = "raft.grpc";

  interface TLS {
    String PREFIX = GrpcConfigKeys.PREFIX + ".tls";

    @Deprecated
    static void setEnabled(RaftProperties properties, boolean enabled) {
      LOG.warn("This method has no effect. Use setConf(Parameters, GrpcTlsConfig) instead.");
    }

    @Deprecated
    static void setMutualAuthnEnabled(RaftProperties properties, boolean mutualAuthnEnabled) {
      LOG.warn("This method has no effect. Use setConf(Parameters, GrpcTlsConfig) instead.");
    }

    @Deprecated
    static void setPrivateKeyFileName(RaftProperties properties, String privateKeyFileName) {
      LOG.warn("This method has no effect. Use setConf(Parameters, GrpcTlsConfig) instead.");
    }

    @Deprecated
    static void setCertChainFileName(RaftProperties properties, String certChainFileName) {
      LOG.warn("This method has no effect. Use setConf(Parameters, GrpcTlsConfig) instead.");
    }

    @Deprecated
    static void setTrustStore(RaftProperties properties, String trustStore) {
      LOG.warn("This method has no effect. Use setConf(Parameters, GrpcTlsConfig) instead.");
    }

    String CONF_PARAMETER = PREFIX + ".conf";
    Class<GrpcTlsConfig> CONF_CLASS = GrpcTlsConfig.class;
    static GrpcTlsConfig conf(Parameters parameters) {
      return parameters != null ? parameters.get(CONF_PARAMETER, CONF_CLASS): null;
    }
    static void setConf(Parameters parameters, GrpcTlsConfig conf) {
      parameters.put(CONF_PARAMETER, conf, GrpcTlsConfig.class);
    }
  }

  interface Admin {
    String PREFIX = GrpcConfigKeys.PREFIX + ".admin";

    String HOST_KEY = PREFIX + ".host";
    String HOST_DEFAULT = null;
    static String host(RaftProperties properties) {
      final String fallbackServerHost = Server.host(properties, null);
      return get(properties::get, HOST_KEY, HOST_DEFAULT, Server.HOST_KEY, fallbackServerHost, getDefaultLog());
    }
    static void setHost(RaftProperties properties, String host) {
      set(properties::set, HOST_KEY, host);
    }

    String PORT_KEY = PREFIX + ".port";
    int PORT_DEFAULT = -1;
    static int port(RaftProperties properties) {
      final int fallbackServerPort = Server.port(properties, null);
      return getInt(properties::getInt, PORT_KEY, PORT_DEFAULT, Server.PORT_KEY, fallbackServerPort,
          getDefaultLog(), requireMin(-1), requireMax(65536));
    }
    static void setPort(RaftProperties properties, int port) {
      setInt(properties::setInt, PORT_KEY, port);
    }

    String TLS_CONF_PARAMETER = PREFIX + ".tls.conf";
    Class<GrpcTlsConfig> TLS_CONF_CLASS = TLS.CONF_CLASS;
    static GrpcTlsConfig tlsConf(Parameters parameters) {
      return parameters != null ? parameters.get(TLS_CONF_PARAMETER, TLS_CONF_CLASS): null;
    }
    static void setTlsConf(Parameters parameters, GrpcTlsConfig conf) {
      parameters.put(TLS_CONF_PARAMETER, conf, TLS_CONF_CLASS);
    }
  }

  interface Client {
    String PREFIX = GrpcConfigKeys.PREFIX + ".client";

    String HOST_KEY = PREFIX + ".host";
    String HOST_DEFAULT = null;
    static String host(RaftProperties properties) {
      final String fallbackServerHost = Server.host(properties, null);
      return get(properties::get, HOST_KEY, HOST_DEFAULT, Server.HOST_KEY, fallbackServerHost, getDefaultLog());
    }
    static void setHost(RaftProperties properties, String host) {
      set(properties::set, HOST_KEY, host);
    }

    String PORT_KEY = PREFIX + ".port";
    int PORT_DEFAULT = -1;
    static int port(RaftProperties properties) {
      final int fallbackServerPort = Server.port(properties, null);
      return getInt(properties::getInt, PORT_KEY, PORT_DEFAULT, Server.PORT_KEY, fallbackServerPort,
          getDefaultLog(), requireMin(-1), requireMax(65536));
    }
    static void setPort(RaftProperties properties, int port) {
      setInt(properties::setInt, PORT_KEY, port);
    }

    String TLS_CONF_PARAMETER = PREFIX + ".tls.conf";
    Class<GrpcTlsConfig> TLS_CONF_CLASS = TLS.CONF_CLASS;
    static GrpcTlsConfig tlsConf(Parameters parameters) {
      return parameters != null ? parameters.get(TLS_CONF_PARAMETER, TLS_CONF_CLASS): null;
    }
    static void setTlsConf(Parameters parameters, GrpcTlsConfig conf) {
      parameters.put(TLS_CONF_PARAMETER, conf, TLS_CONF_CLASS);
    }
  }

  interface Server {
    String PREFIX = GrpcConfigKeys.PREFIX + ".server";

    String HOST_KEY = PREFIX + ".host";
    String HOST_DEFAULT = null;
    static String host(RaftProperties properties) {
      return host(properties, getDefaultLog());
    }

    static String host(RaftProperties properties, Consumer<String> logger) {
      return get(properties::get, HOST_KEY, HOST_DEFAULT, logger);
    }

    static void setHost(RaftProperties properties, String host) {
      set(properties::set, HOST_KEY, host);
    }

    String PORT_KEY = PREFIX + ".port";
    int PORT_DEFAULT = 0;
    static int port(RaftProperties properties) {
      return port(properties, getDefaultLog());
    }

    static int port(RaftProperties properties, Consumer<String> logger) {
      return getInt(properties::getInt,
          PORT_KEY, PORT_DEFAULT, logger, requireMin(0), requireMax(65536));
    }

    static void setPort(RaftProperties properties, int port) {
      setInt(properties::setInt, PORT_KEY, port);
    }

    String ASYNC_REQUEST_THREAD_POOL_CACHED_KEY = PREFIX + ".async.request.thread.pool.cached";
    boolean ASYNC_REQUEST_THREAD_POOL_CACHED_DEFAULT = true;
    static boolean asyncRequestThreadPoolCached(RaftProperties properties) {
      return getBoolean(properties::getBoolean, ASYNC_REQUEST_THREAD_POOL_CACHED_KEY,
          ASYNC_REQUEST_THREAD_POOL_CACHED_DEFAULT, getDefaultLog());
    }
    static void setAsyncRequestThreadPoolCached(RaftProperties properties, boolean useCached) {
      setBoolean(properties::setBoolean, ASYNC_REQUEST_THREAD_POOL_CACHED_KEY, useCached);
    }

    String ASYNC_REQUEST_THREAD_POOL_SIZE_KEY = PREFIX + ".async.request.thread.pool.size";
    int ASYNC_REQUEST_THREAD_POOL_SIZE_DEFAULT = 32;
    static int asyncRequestThreadPoolSize(RaftProperties properties) {
      return getInt(properties::getInt, ASYNC_REQUEST_THREAD_POOL_SIZE_KEY,
          ASYNC_REQUEST_THREAD_POOL_SIZE_DEFAULT, getDefaultLog(),
          requireMin(0), requireMax(65536));
    }
    static void setAsyncRequestThreadPoolSize(RaftProperties properties, int port) {
      setInt(properties::setInt, ASYNC_REQUEST_THREAD_POOL_SIZE_KEY, port);
    }

    String LEADER_OUTSTANDING_APPENDS_MAX_KEY = PREFIX + ".leader.outstanding.appends.max";
    int LEADER_OUTSTANDING_APPENDS_MAX_DEFAULT = 8;
    static int leaderOutstandingAppendsMax(RaftProperties properties) {
      return getInt(properties::getInt,
          LEADER_OUTSTANDING_APPENDS_MAX_KEY, LEADER_OUTSTANDING_APPENDS_MAX_DEFAULT, getDefaultLog(), requireMin(0));
    }
    static void setLeaderOutstandingAppendsMax(RaftProperties properties, int maxAppend) {
      setInt(properties::setInt, LEADER_OUTSTANDING_APPENDS_MAX_KEY, maxAppend);
    }

    String INSTALL_SNAPSHOT_REQUEST_ELEMENT_LIMIT_KEY = PREFIX + ".install_snapshot.request.element-limit";
    int INSTALL_SNAPSHOT_REQUEST_ELEMENT_LIMIT_DEFAULT = 8;
    static int installSnapshotRequestElementLimit(RaftProperties properties) {
      return getInt(properties::getInt, INSTALL_SNAPSHOT_REQUEST_ELEMENT_LIMIT_KEY,
          INSTALL_SNAPSHOT_REQUEST_ELEMENT_LIMIT_DEFAULT, getDefaultLog(), requireMin(0));
    }
    static void setInstallSnapshotRequestElementLimit(RaftProperties properties, int elementLimit) {
      setInt(properties::setInt, INSTALL_SNAPSHOT_REQUEST_ELEMENT_LIMIT_KEY, elementLimit);
    }

    String INSTALL_SNAPSHOT_REQUEST_TIMEOUT_KEY = PREFIX + ".install_snapshot.request.timeout";
    TimeDuration INSTALL_SNAPSHOT_REQUEST_TIMEOUT_DEFAULT = RaftServerConfigKeys.Rpc.REQUEST_TIMEOUT_DEFAULT;
    static TimeDuration installSnapshotRequestTimeout(RaftProperties properties) {
      return getTimeDuration(properties.getTimeDuration(INSTALL_SNAPSHOT_REQUEST_TIMEOUT_DEFAULT.getUnit()),
          INSTALL_SNAPSHOT_REQUEST_TIMEOUT_KEY, INSTALL_SNAPSHOT_REQUEST_TIMEOUT_DEFAULT, getDefaultLog());
    }
    static void setInstallSnapshotRequestTimeout(RaftProperties properties,
                                                 TimeDuration installSnapshotRequestTimeout) {
      setTimeDuration(properties::setTimeDuration,
          INSTALL_SNAPSHOT_REQUEST_TIMEOUT_KEY, installSnapshotRequestTimeout);
    }

    String HEARTBEAT_CHANNEL_KEY = PREFIX + ".heartbeat.channel";
    boolean HEARTBEAT_CHANNEL_DEFAULT = true;
    static boolean heartbeatChannel(RaftProperties properties) {
      return getBoolean(properties::getBoolean, HEARTBEAT_CHANNEL_KEY,
              HEARTBEAT_CHANNEL_DEFAULT, getDefaultLog());
    }
    static void setHeartbeatChannel(RaftProperties properties, boolean useSeparate) {
      setBoolean(properties::setBoolean, HEARTBEAT_CHANNEL_KEY, useSeparate);
    }

    String LOG_MESSAGE_BATCH_DURATION_KEY = PREFIX + ".log-message.batch.duration";
    TimeDuration LOG_MESSAGE_BATCH_DURATION_DEFAULT = TimeDuration.valueOf(5, TimeUnit.SECONDS);
    static TimeDuration logMessageBatchDuration(RaftProperties properties) {
      return getTimeDuration(properties.getTimeDuration(LOG_MESSAGE_BATCH_DURATION_DEFAULT.getUnit()),
          LOG_MESSAGE_BATCH_DURATION_KEY, LOG_MESSAGE_BATCH_DURATION_DEFAULT, getDefaultLog());
    }
    static void setLogMessageBatchDuration(RaftProperties properties,
                                           TimeDuration logMessageBatchDuration) {
      setTimeDuration(properties::setTimeDuration,
          LOG_MESSAGE_BATCH_DURATION_KEY, logMessageBatchDuration);
    }

    String SERVICES_CUSTOMIZER_PARAMETER = PREFIX + ".services.customizer";
    Class<GrpcServices.Customizer> SERVICES_CUSTOMIZER_CLASS = GrpcServices.Customizer.class;
    static GrpcServices.Customizer servicesCustomizer(Parameters parameters) {
      return parameters == null ? null
          : parameters.get(SERVICES_CUSTOMIZER_PARAMETER, SERVICES_CUSTOMIZER_CLASS);
    }
    static void setServicesCustomizer(Parameters parameters, GrpcServices.Customizer customizer) {
      parameters.put(SERVICES_CUSTOMIZER_PARAMETER, customizer, SERVICES_CUSTOMIZER_CLASS);
    }

    String TLS_CONF_PARAMETER = PREFIX + ".tls.conf";
    Class<GrpcTlsConfig> TLS_CONF_CLASS = TLS.CONF_CLASS;
    static GrpcTlsConfig tlsConf(Parameters parameters) {
      return parameters != null ? parameters.get(TLS_CONF_PARAMETER, TLS_CONF_CLASS): null;
    }
    static void setTlsConf(Parameters parameters, GrpcTlsConfig conf) {
      parameters.put(TLS_CONF_PARAMETER, conf, TLS_CONF_CLASS);
    }
  }

  String MESSAGE_SIZE_MAX_KEY = PREFIX + ".message.size.max";
  SizeInBytes MESSAGE_SIZE_MAX_DEFAULT = SizeInBytes.valueOf("64MB");
  static SizeInBytes messageSizeMax(RaftProperties properties, Consumer<String> logger) {
    return getSizeInBytes(properties::getSizeInBytes,
        MESSAGE_SIZE_MAX_KEY, MESSAGE_SIZE_MAX_DEFAULT, logger);
  }
  static void setMessageSizeMax(RaftProperties properties, SizeInBytes maxMessageSize) {
    setSizeInBytes(properties::set, MESSAGE_SIZE_MAX_KEY, maxMessageSize);
  }

  String FLOW_CONTROL_WINDOW_KEY = PREFIX + ".flow.control.window";
  SizeInBytes FLOW_CONTROL_WINDOW_DEFAULT = SizeInBytes.valueOf("1MB");
  static SizeInBytes flowControlWindow(RaftProperties properties, Consumer<String> logger) {
    return getSizeInBytes(properties::getSizeInBytes,
        FLOW_CONTROL_WINDOW_KEY, FLOW_CONTROL_WINDOW_DEFAULT, logger);
  }
  static void setFlowControlWindow(RaftProperties properties, SizeInBytes flowControlWindowSize) {
    setSizeInBytes(properties::set, FLOW_CONTROL_WINDOW_KEY, flowControlWindowSize);
  }

  static void main(String[] args) {
    printAll(GrpcConfigKeys.class);
  }
}
