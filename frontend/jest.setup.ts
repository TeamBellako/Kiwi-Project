import '@testing-library/jest-dom';
import {TextDecoder, TextEncoder} from "util";

(global as any).TextEncoder = TextEncoder;
(global as any).TextDecoder = TextDecoder;