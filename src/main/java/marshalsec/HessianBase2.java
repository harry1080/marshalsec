/* MIT License

Copyright (c) 2017 Moritz Bechler

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package marshalsec;


import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.io.UnsafeSerializer;
import com.caucho.hessian.io.WriteReplaceSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import marshalsec.gadgets.Resin;
import marshalsec.gadgets.Rome;
import marshalsec.gadgets.SpringAbstractBeanFactoryPointcutAdvisor;
import marshalsec.gadgets.SpringPartiallyComparableAdvisorHolder;
import marshalsec.gadgets.XBean;
import marshalsec.gadgets.XBean2;


/**
 * 
 * Not applicable:
 * - BindingEnumeration/LazySearchEnumeration/ServiceLoader/ImageIO: custom conversion of Iterator
 * 
 * @author threedr3am
 *
 */
public abstract class HessianBase2 extends MarshallerBase<byte[]>
        implements SpringAbstractBeanFactoryPointcutAdvisor, Rome,
    XBean2, Resin {

    /**
     * {@inheritDoc}
     *
     * @see MarshallerBase#marshal(Object)
     */
    @Override
    public byte[] marshal ( Object o ) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AbstractHessianOutput out = createOutput(bos);
        NoWriteReplaceSerializerFactory sf = new NoWriteReplaceSerializerFactory();
        sf.setAllowNonSerializable(true);
        out.setSerializerFactory(sf);
        out.writeObject(o);
        out.close();
        return bos.toByteArray();
    }


    /**
     * {@inheritDoc}
     *
     * @see MarshallerBase#unmarshal(Object)
     */
    @Override
    public Object unmarshal ( byte[] data ) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        AbstractHessianInput in = createInput(bis);
        return in.readObject();
    }


    /**
     * @param bos
     * @return
     */
    protected abstract AbstractHessianOutput createOutput ( ByteArrayOutputStream bos );


    protected abstract AbstractHessianInput createInput ( ByteArrayInputStream bos );

    public static class NoWriteReplaceSerializerFactory extends SerializerFactory {

        /**
         * {@inheritDoc}
         *
         * @see SerializerFactory#getObjectSerializer(Class)
         */
        @Override
        public Serializer getObjectSerializer ( Class<?> cl ) throws HessianProtocolException {
            return super.getObjectSerializer(cl);
        }


        /**
         * {@inheritDoc}
         *
         * @see SerializerFactory#getSerializer(Class)
         */
        @Override
        public Serializer getSerializer ( Class cl ) throws HessianProtocolException {
            Serializer serializer = super.getSerializer(cl);

            if ( serializer instanceof WriteReplaceSerializer ) {
                return UnsafeSerializer.create(cl);
            }
            return serializer;
        }

    }

}