<?php

namespace StephenHill;

use InvalidArgumentException;

/**
 * @package      StephenHill\Base58
 * @author       Stephen Hill <stephen@gatekiller.co.uk>
 * @copyright    2014 Stephen Hill <stephen@gatekiller.co.uk>
 * @license      http://www.opensource.org/licenses/MIT    The MIT License
 * @link         https://github.com/stephen-hill/base58php
 * @since        Release v1.0.0
 */
class Base58
{
    /**
     * @var StephenHill\ServiceInterface;
     * @since v1.1.0
     */
    protected $service;

    /**
     * Constructor
     *
     * @param string           $alphabet optional
     * @param ServiceInterface $service  optional
     * @since v1.0.0
     * @since v1.1.0 Added the optional $service argument.
     */
    public function __construct(
        $alphabet = null,
        ServiceInterface $service = null
    ) {
        // Handle null alphabet
        if (is_null($alphabet) === true) {
            $alphabet = '123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz';
        }

        // Type validation
        if (is_string($alphabet) === false) {
            throw new InvalidArgumentException('Argument $alphabet must be a string.');
        }

        // The alphabet must contain 58 characters
        if (strlen($alphabet) !== 58) {
            throw new InvalidArgumentException('Argument $alphabet must contain 58 characters.');
        }

        // Provide a default service if one isn't injected
        if ($service === null) {
            // Check for GMP support first
            if (function_exists('\gmp_init') === true) {
                $service = new GMPService($alphabet);
            }
            else if (function_exists('\bcmul') === true) {
                $service = new BCMathService($alphabet);
            }
            else {
                throw new \Exception('Please install the BC Math or GMP extension.');
            }
        }

        $this->service = $service;
    }

    /**
     * Encode a string into base58.
     *
     * @param  string $string The string you wish to encode.
     * @since v1.0.0
     * @return string The Base58 encoded string.
     */
    public function encode($string)
    {
        return $this->service->encode($string);
    }

    /**
     * Decode base58 into a PHP string.
     *
     * @param  string $base58 The base58 encoded string.
     * @since v1.0.0
     * @return string Returns the decoded string.
     */
    public function decode($base58)
    {
        return $this->service->decode($base58);
    }
}

class BCMathService implements ServiceInterface
{
    /**
     * @var string
     * @since v1.1.0
     */
    protected $alphabet;

    /**
     * @var int
     * @since v1.1.0
     */
    protected $base;

    /**
     * Constructor
     *
     * @param string $alphabet optional
     * @since v1.1.0
     */
    public function __construct($alphabet = null)
    {
        // Handle null alphabet
        if (is_null($alphabet) === true) {
            $alphabet = '123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz';
        }

        // Type validation
        if (is_string($alphabet) === false) {
            throw new InvalidArgumentException('Argument $alphabet must be a string.');
        }

        // The alphabet must contain 58 characters
        if (strlen($alphabet) !== 58) {
            throw new InvalidArgumentException('Argument $alphabet must contain 58 characters.');
        }

        $this->alphabet = $alphabet;
        $this->base = strlen($alphabet);
    }
    /**
     * Encode a string into base58.
     *
     * @param  string $string The string you wish to encode.
     * @since Release v1.1.0
     * @return string The Base58 encoded string.
     */
    public function encode($string)
    {
        // Type validation
        if (is_string($string) === false) {
            throw new InvalidArgumentException('Argument $string must be a string.');
        }

        // If the string is empty, then the encoded string is obviously empty
        if (strlen($string) === 0) {
            return '';
        }

        // Strings in PHP are essentially 8-bit byte arrays
        // so lets convert the string into a PHP array
        $bytes = array_values(unpack('C*', $string));

        // Now we need to convert the byte array into an arbitrary-precision decimal
        // We basically do this by performing a base256 to base10 conversion
        $decimal = $bytes[0];

        for ($i = 1, $l = count($bytes); $i < $l; $i++) {
            $decimal = bcmul($decimal, 256);
            $decimal = bcadd($decimal, $bytes[$i]);
        }

        // This loop now performs base 10 to base 58 conversion
        // The remainder or modulo on each loop becomes a base 58 character
        $output = '';
        while ($decimal >= $this->base) {
            $div = bcdiv($decimal, $this->base, 0);
            $mod = bcmod($decimal, $this->base);
            $output .= $this->alphabet[$mod];
            $decimal = $div;
        }

        // If there's still a remainder, append it
        if ($decimal > 0) {
            $output .= $this->alphabet[$decimal];
        }

        // Now we need to reverse the encoded data
        $output = strrev($output);

        // Now we need to add leading zeros
        foreach ($bytes as $byte) {
            if ($byte === 0) {
                $output = $this->alphabet[0] . $output;
                continue;
            }
            break;
        }

        return (string) $output;
    }

    /**
     * Decode base58 into a PHP string.
     *
     * @param  string $base58 The base58 encoded string.
     * @since Release v1.1.0
     * @return string Returns the decoded string.
     */
    public function decode($base58)
    {
        // Type Validation
        if (is_string($base58) === false) {
            throw new InvalidArgumentException('Argument $base58 must be a string.');
        }

        // If the string is empty, then the decoded string is obviously empty
        if (strlen($base58) === 0) {
            return '';
        }

        $indexes = array_flip(str_split($this->alphabet));
        $chars = str_split($base58);

        // Check for invalid characters in the supplied base58 string
        foreach ($chars as $char) {
            if (isset($indexes[$char]) === false) {
                throw new InvalidArgumentException('Argument $base58 contains invalid characters. ($char: "'.$char.'" | $base58: "'.$base58.'") ');
            }
        }

        // Convert from base58 to base10
        $decimal = $indexes[$chars[0]];

        for ($i = 1, $l = count($chars); $i < $l; $i++) {
            $decimal = bcmul($decimal, $this->base);
            $decimal = bcadd($decimal, $indexes[$chars[$i]]);
        }

        // Convert from base10 to base256 (8-bit byte array)
        $output = '';
        while ($decimal > 0) {
            $byte = bcmod($decimal, 256);
            $output = pack('C', $byte) . $output;
            $decimal = bcdiv($decimal, 256, 0);
        }

        // Now we need to add leading zeros
        foreach ($chars as $char) {
            if ($indexes[$char] === 0) {
                $output = "\x00" . $output;
                continue;
            }
            break;
        }

        return $output;
    }
}

interface ServiceInterface
{
    /**
     * Encode a string into base58.
     *
     * @param  string $string The string you wish to encode.
     * @since v1.1.0
     * @return string The Base58 encoded string.
     */
    public function encode($string);

    /**
     * Decode base58 into a PHP string.
     *
     * @param  string $base58 The base58 encoded string.
     * @since v1.1.0
     * @return string Returns the decoded string.
     */
    public function decode($base58);
}

class GMPService implements ServiceInterface
{
    /**
     * @var string
     * @since v1.1.0
     */
    protected $alphabet;

    /**
     * @var int
     * @since v1.1.0
     */
    protected $base;

    /**
     * Constructor
     *
     * @param string $alphabet optional
     * @since v1.1.0
     */
    public function __construct($alphabet = null)
    {
        // Handle null alphabet
        if (is_null($alphabet) === true) {
            $alphabet = '123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz';
        }

        // Type validation
        if (is_string($alphabet) === false) {
            throw new InvalidArgumentException('Argument $alphabet must be a string.');
        }

        // The alphabet must contain 58 characters
        if (strlen($alphabet) !== 58) {
            throw new InvalidArgumentException('Argument $alphabet must contain 58 characters.');
        }

        $this->alphabet = $alphabet;
        $this->base = strlen($alphabet);
    }
    /**
     * Encode a string into base58.
     *
     * @param  string $string The string you wish to encode.
     * @since Release v1.1.0
     * @return string The Base58 encoded string.
     */
    public function encode($string)
    {
        // Type validation
        if (is_string($string) === false) {
            throw new InvalidArgumentException('Argument $string must be a string.');
        }

        // If the string is empty, then the encoded string is obviously empty
        if (strlen($string) === 0) {
            return '';
        }

        // Now we need to convert the byte array into an arbitrary-precision decimal
        // We basically do this by performing a base256 to base10 conversion
        $hex = unpack('H*', $string);
        $hex = reset($hex);
        $decimal = gmp_init($hex, 16);

        // This loop now performs base 10 to base 58 conversion
        // The remainder or modulo on each loop becomes a base 58 character
        $output = '';
        while (gmp_cmp($decimal, $this->base) >= 0) {
            list($decimal, $mod) = gmp_div_qr($decimal, $this->base);
            $output .= $this->alphabet[gmp_intval($mod)];
        }

        // If there's still a remainder, append it
        if (gmp_cmp($decimal, 0) > 0) {
            $output .= $this->alphabet[gmp_intval($decimal)];
        }

        // Now we need to reverse the encoded data
        $output = strrev($output);

        // Now we need to add leading zeros
        $bytes = str_split($string);
        foreach ($bytes as $byte) {
            if ($byte === "\x00") {
                $output = $this->alphabet[0] . $output;
                continue;
            }
            break;
        }

        return (string) $output;
    }

    /**
     * Decode base58 into a PHP string.
     *
     * @param  string $base58 The base58 encoded string.
     * @since Release v1.1.0
     * @return string Returns the decoded string.
     */
    public function decode($base58)
    {
        // Type Validation
        if (is_string($base58) === false) {
            throw new InvalidArgumentException('Argument $base58 must be a string.');
        }

        // If the string is empty, then the decoded string is obviously empty
        if (strlen($base58) === 0) {
            return '';
        }

        $indexes = array_flip(str_split($this->alphabet));
        $chars = str_split($base58);

        // Check for invalid characters in the supplied base58 string
        foreach ($chars as $char) {
            if (isset($indexes[$char]) === false) {
                throw new InvalidArgumentException('Argument $base58 contains invalid characters.');
            }
        }

        // Convert from base58 to base10
        $decimal = gmp_init($indexes[$chars[0]], 10);

        for ($i = 1, $l = count($chars); $i < $l; $i++) {
            $decimal = gmp_mul($decimal, $this->base);
            $decimal = gmp_add($decimal, $indexes[$chars[$i]]);
        }

        // Convert from base10 to base256 (8-bit byte array)
        $output = '';
        while (gmp_cmp($decimal, 0) > 0) {
            list($decimal, $byte) = gmp_div_qr($decimal, 256);
            $output = pack('C', gmp_intval($byte)) . $output;
        }

        // Now we need to add leading zeros
        foreach ($chars as $char) {
            if ($indexes[$char] === 0) {
                $output = "\x00" . $output;
                continue;
            }
            break;
        }

        return $output;
    }
}

//$a = new Base58();
//$s = $a->encode(hex2bin("acdeaa"));
////$s = $a->encode("acdeaa");
//echo $s;

//$s2 = $a->decode("AFc5jB4");
//echo $s2;