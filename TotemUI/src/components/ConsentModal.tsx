import React, { useState } from 'react';
import { Shield, Check, X } from 'lucide-react';

interface ConsentModalProps {
  isOpen: boolean;
  onAccept: () => void;
  onDecline: () => void;
  patientName?: string;
}

/**
 * Modal de Consentimento LGPD
 * Conformidade com LGPD Art. 8 (Consentimento)
 */
export default function ConsentModal({ isOpen, onAccept, onDecline, patientName }: ConsentModalProps) {
  const [hasRead, setHasRead] = useState(false);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-hidden flex flex-col">
        <div className="bg-blue-600 text-white p-6">
          <div className="flex items-center space-x-3">
            <Shield className="w-8 h-8" />
            <div>
              <h2 className="text-2xl font-bold">Termo de Consentimento LGPD</h2>
              <p className="text-blue-100 text-sm mt-1">Lei Geral de Proteção de Dados</p>
            </div>
          </div>
        </div>

        <div className="p-6 overflow-y-auto flex-1">
          {patientName && (
            <p className="mb-4 text-gray-700">
              <strong>{patientName}</strong>, para prosseguir com o agendamento, precisamos do seu consentimento para o tratamento de dados pessoais.
            </p>
          )}

          <div className="space-y-4 text-sm text-gray-600">
            <section>
              <h3 className="font-bold text-gray-800 text-base mb-2">1. Coleta e Uso de Dados</h3>
              <p>
                Coletaremos e utilizaremos seus dados pessoais (nome, CPF, telefone, email, endereço e data de nascimento) 
                exclusivamente para:
              </p>
              <ul className="list-disc ml-6 mt-2 space-y-1">
                <li>Agendamento e realização de consultas médicas</li>
                <li>Processamento de pagamentos via parceiro LunaPay (fora do totem)</li>
                <li>Comunicação sobre suas consultas e serviços</li>
                <li>Cumprimento de obrigações legais e regulatórias</li>
              </ul>
            </section>

            <section>
              <h3 className="font-bold text-gray-800 text-base mb-2">2. Bases Legais (LGPD Art. 7)</h3>
              <ul className="list-disc ml-6 space-y-1">
                <li>Execução de contrato (prestação de serviços médicos)</li>
                <li>Seu consentimento livre e informado</li>
                <li>Cumprimento de obrigação legal (CFM, ANS)</li>
              </ul>
            </section>

            <section>
              <h3 className="font-bold text-gray-800 text-base mb-2">3. Compartilhamento de Dados</h3>
              <p>
                Seus dados NÃO serão compartilhados com terceiros, exceto:
              </p>
              <ul className="list-disc ml-6 mt-2 space-y-1">
                <li>Processador de pagamento (LunaPay) para finalizar transações fora do totem</li>
                <li>Autoridades legais (quando exigido por lei)</li>
              </ul>
            </section>

            <section>
              <h3 className="font-bold text-gray-800 text-base mb-2">4. Seus Direitos (LGPD Art. 18)</h3>
              <p>Você tem direito a:</p>
              <ul className="list-disc ml-6 mt-2 space-y-1">
                <li>Confirmar se tratamos seus dados</li>
                <li>Acessar seus dados pessoais</li>
                <li>Corrigir dados incompletos ou desatualizados</li>
                <li>Solicitar anonimização ou eliminação de dados</li>
                <li>Revogar este consentimento a qualquer momento</li>
                <li>Obter portabilidade dos seus dados</li>
              </ul>
            </section>

            <section>
              <h3 className="font-bold text-gray-800 text-base mb-2">5. Segurança</h3>
              <p>Implementamos as seguintes medidas de segurança:</p>
              <ul className="list-disc ml-6 mt-2 space-y-1">
                <li>Criptografia de dados em trânsito (HTTPS/TLS)</li>
                <li>Criptografia de dados sensíveis em repouso (AES-256-GCM)</li>
                <li>Controle de acesso baseado em funções</li>
                <li>Logs de auditoria de todos os acessos a dados</li>
                <li>Senhas protegidas com hash BCrypt</li>
              </ul>
            </section>

            <section>
              <h3 className="font-bold text-gray-800 text-base mb-2">6. Retenção de Dados</h3>
              <p>
                Seus dados serão mantidos pelo período necessário para prestação dos serviços e 
                cumprimento de obrigações legais. Prontuários médicos são mantidos por no mínimo 
                20 anos conforme determinação do CFM.
              </p>
            </section>

            <section>
              <h3 className="font-bold text-gray-800 text-base mb-2">7. Contato do Encarregado (DPO)</h3>
              <p>
                Para exercer seus direitos ou esclarecer dúvidas sobre o tratamento de seus dados:
              </p>
              <p className="mt-2">
                <strong>Email:</strong> dpo@lunavita.com<br />
                <strong>Telefone:</strong> (85) 4002-8922
              </p>
            </section>
          </div>

          <div className="mt-6 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
            <label className="flex items-start space-x-3 cursor-pointer">
              <input
                type="checkbox"
                checked={hasRead}
                onChange={(e) => setHasRead(e.target.checked)}
                className="mt-1 w-5 h-5 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
              <span className="text-sm text-gray-700">
                Declaro que li e compreendi todos os termos acima e concordo com o tratamento dos meus 
                dados pessoais conforme descrito neste documento.
              </span>
            </label>
          </div>
        </div>

        <div className="p-6 bg-gray-50 border-t flex space-x-3">
          <button
            onClick={onDecline}
            className="flex-1 px-6 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 
                     transition-colors flex items-center justify-center space-x-2"
          >
            <X className="w-5 h-5" />
            <span>Não Aceito</span>
          </button>
          <button
            onClick={onAccept}
            disabled={!hasRead}
            className={`flex-1 px-6 py-3 rounded-lg transition-colors flex items-center justify-center space-x-2
                     ${hasRead 
                       ? 'bg-blue-600 text-white hover:bg-blue-700' 
                       : 'bg-gray-300 text-gray-500 cursor-not-allowed'}`}
          >
            <Check className="w-5 h-5" />
            <span>Aceito e Concordo</span>
          </button>
        </div>
      </div>
    </div>
  );
}
