package net.dougqh.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Reflector< T > {
	private final T object;
	private final Class< ? > aClass;
	
	public static final < T > Reflector< T > wrap( final T object ) {
		return new Reflector< T >( object );
	}
	
	public static final < T > Reflector< T > newInstance(
		final String className,
		final Object... args )
	{
		return new Reflector< T >( newInstanceImpl( 
			Reflector.< T >loadClass( className ), args ) );
	}
	
	public static final < T > Reflector< T > newInstance(
		final Class< T > aClass,
		final Object... args )
	{
		return new Reflector< T >( newInstanceImpl( aClass, args ) );
	}
	
	public static final Reflector< ? > newInnerInstance(
		final Object outerObject,
		final String innerClassName )
	{
		return new Reflector< Object >( newInnerInstanceImpl( outerObject, innerClassName ) );
	}
	
	private Reflector( final T object ) {
		this.object = object;
		this.aClass = object.getClass();
	}
	
	public final T getObject() {
		return this.object;
	}
	
	@SuppressWarnings( "hiding" )
	public final < T > T invoke( final String name, final Object... args ) {
		Method method = this.getMethod( name );
		method.setAccessible( true );
		
		try {
			@SuppressWarnings("unchecked")
			T result = (T)method.invoke( this.object, args );
			return result;
		} catch ( IllegalAccessException e ) {
			throw new IllegalStateException( e );
		} catch ( InvocationTargetException e ) {
			throw new IllegalStateException( e );
		}
	}
	
	public final void setField( final String property, final Object value ) {
		try {
			Field field = this.aClass.getDeclaredField( property );
			field.setAccessible( true );
			field.set( this.object, value );
		} catch ( SecurityException e ) {
			throw new IllegalStateException( e );
		} catch ( NoSuchFieldException e ) {
			throw new IllegalStateException( e );
		} catch ( IllegalAccessException e ) {
			throw new IllegalStateException( e );
		}
	}
	
	@SuppressWarnings( "hiding" )
	public final < T > T getField( final String property ) {
		try {
			Field field = this.aClass.getDeclaredField( property );
			field.setAccessible( true );
			
			@SuppressWarnings( "unchecked" )
			T result = (T)field.get( this.object );
			return result;
		} catch ( SecurityException e ) {
			throw new IllegalStateException( e );
		} catch ( NoSuchFieldException e ) {
			throw new IllegalStateException( e );
		} catch ( IllegalAccessException e ) {
			throw new IllegalStateException( e );
		}
	}
	
	private final Method getMethod( final String name ) {
		for ( Method method : this.aClass.getMethods() ) {
			if ( method.getName().equals( name ) ) {
				return method;
			}
		}
		
		for ( Method method : this.aClass.getDeclaredMethods() ) {
			if ( method.getName().equals( name ) ) {
				return method;
			}
		}
		
		throw new IllegalStateException( new NoSuchMethodException( name ) );
	}
	
	public final Reflector< ? > newInnerInstance( final String innerClassName ) {
		return Reflector.newInnerInstance( this.object, innerClassName );
	}
	
	private static final < T > Class< T > loadClass( final String className ) {
		try {
			@SuppressWarnings( "unchecked" )
			Class< T > result = (Class< T >)Class.forName( className );
			return result;
		} catch ( ClassNotFoundException e ) {
			throw new IllegalStateException( e );
		}
	}
	
	private static final < T > T newInstanceImpl(
		final Class< T > aClass,
		final Object... args )
	{
		try {
			Constructor< T > constructor = constructor( aClass, args.length );
			constructor.setAccessible( true );
			return constructor.newInstance();
		} catch ( SecurityException e ) {
			throw new IllegalStateException( e );
		} catch ( InstantiationException e ) {
			throw new IllegalStateException( e );
		} catch ( IllegalAccessException e ) {
			throw new IllegalStateException( e );
		} catch ( InvocationTargetException e ) {
			throw new IllegalStateException( e );
		} catch ( NoSuchMethodException e ) {
			throw new IllegalStateException( e );
		}
	}
	
	private static final < T > Constructor< T > constructor(
		final Class< T > aClass,
		final int arity )
		throws NoSuchMethodException
	{
		for ( Constructor<?> constructor : aClass.getDeclaredConstructors() ) {
			if ( constructor.getParameterTypes().length == arity ) {
				@SuppressWarnings( "unchecked" )
				Constructor< T > castConstructor = (Constructor< T >)constructor;
				return castConstructor;
			}
		}
		throw new NoSuchMethodException( "No constructor with arity: " + arity );
	}

	private static final Object newInnerInstanceImpl(
		final Object outerObject,
		final String className )
	{
		try {
			Class< ? > outerClass = outerObject.getClass();
			Class< ? > innerClass = Class.forName( outerClass.getName() + "$" + className );

			Constructor< ? > constructor = innerClass.getDeclaredConstructor( outerClass );
			constructor.setAccessible( true );
			return constructor.newInstance( outerObject );
		} catch ( ClassNotFoundException e ) {
			throw new IllegalStateException( e );
		} catch ( SecurityException e ) {
			throw new IllegalStateException( e );
		} catch ( InstantiationException e ) {
			throw new IllegalStateException( e );
		} catch ( IllegalAccessException e ) {
			throw new IllegalStateException( e );
		} catch ( InvocationTargetException e ) {
			throw new IllegalStateException( e );
		} catch ( NoSuchMethodException e ) {
			throw new IllegalStateException( e );
		}
	}
}
