package com.serge.pingpong.service;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.serge.pingpong.model.Pong;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Serge Ivanov
 * Kryo serializer for Pong entity
 */
public class PongSerializer implements StreamSerializer<Pong> {

    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = new ThreadLocal<Kryo>() {

        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.register( Pong.class );
            return kryo;
        }
    };

    @Override
    public void write( ObjectDataOutput out, Pong object ) throws IOException {
        Kryo kryo = KRYO_THREAD_LOCAL.get();

        Output output = new Output( (OutputStream) out );
        kryo.writeClassAndObject( output, object );
        output.flush();
    }

    @Override
    public Pong read( ObjectDataInput in ) throws IOException {
        Input input = new Input( (InputStream) in );
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        return (Pong) kryo.readClassAndObject( input );
    }

    @Override
    public int getTypeId() {
        return 1;
    }

    @Override
    public void destroy() {

    }
}
