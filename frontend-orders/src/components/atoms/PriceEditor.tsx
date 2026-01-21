import { NumericFormat, type NumericFormatProps } from 'react-number-format';
import { TextField, type TextFieldProps } from "@mui/material";
import type { Currency } from "../../types";

type BaseNumericProps = Omit<NumericFormatProps, 'value' | 'onChange' | 'customInput' | 'size' | 'color'>;

export type PriceEditorProps = BaseNumericProps & {
    currency: Currency;
    value: string | number;
    onChange: (val: string) => void;
    label?: string;
    error?: boolean;
    helperText?: string;
    size?: TextFieldProps['size'];
    color?: TextFieldProps['color'];
    variant?: TextFieldProps['variant'];
}

export const PriceEditor = ({
                                currency,
                                value,
                                onChange,
                                label,
                                error,
                                helperText,
                                size,
                                color,
                                variant,
                                ...props
                            }: PriceEditorProps) => {
    return (
        <NumericFormat
            value={value}
            customInput={TextField}
            thousandSeparator="."
            decimalSeparator=","
            decimalScale={2}
            fixedDecimalScale={true}
            allowNegative={false}
            prefix={`${currency.symbol} `}
            label={label || "Price"}
            variant={variant || "outlined"}
            size={size}
            color={color}
            error={error}
            helperText={helperText}

            onValueChange={(values) => {
                onChange(values.value);
            }}

            {...props}
        />
    );
}